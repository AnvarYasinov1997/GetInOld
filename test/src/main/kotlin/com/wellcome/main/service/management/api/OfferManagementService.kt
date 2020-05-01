package com.wellcome.main.service.management.api

import com.wellcome.main.component.TimestampProvider
import com.wellcome.main.dto.api.newDto.Days
import com.wellcome.main.dto.api.newDto.common.v1.BlockOffersDtoV1
import com.wellcome.main.dto.api.newDto.common.v1.BlockType
import com.wellcome.main.dto.api.newDto.common.v2.BlockOffersDtoV2
import com.wellcome.main.dto.api.newDto.request.v1.FullBlockOfferRequestV1
import com.wellcome.main.dto.api.newDto.response.v1.FullOfferResponseV1
import com.wellcome.main.dto.api.newDto.response.v1.OfferResponseV1
import com.wellcome.main.dto.api.newDto.response.v2.FullOfferResponseV2
import com.wellcome.main.entity.ApplicationConfigType
import com.wellcome.main.entity.institution.Institution
import com.wellcome.main.entity.institution.InstitutionOffer
import com.wellcome.main.service.cache.InstitutionCacheService
import com.wellcome.main.service.extentions.generators.api.common.generateBlockOfferDtoV1List
import com.wellcome.main.service.extentions.generators.api.common.generateOfferDtoV1List
import com.wellcome.main.service.extentions.generators.api.common.generateOfferWorkDayAttributesDtoV1List
import com.wellcome.main.service.extentions.generators.api.common.v2.generateBlockOfferDtoV2List
import com.wellcome.main.service.extentions.generators.api.common.v2.generateOfferDtoV2List
import com.wellcome.main.service.extentions.management.filterByInstitutionCategory
import com.wellcome.main.service.extentions.management.filterNotByInstitutionCategory
import com.wellcome.main.service.facade.ApplicationConfigService
import com.wellcome.main.service.facade.institution.InstitutionCategoryService
import com.wellcome.main.service.facade.institution.InstitutionService
import com.wellcome.main.service.facade.user.UserService
import com.wellcome.main.service.interceptor.StoreReviewInterceptorService
import com.wellcome.main.service.interceptor.UserInterceptorService
import com.wellcome.main.service.interceptor.WorkTimeInterceptorService
import com.wellcome.main.util.enumerators.SearchInstitutionDays
import com.wellcome.main.util.functions.getDay
import com.wellcome.main.util.functions.plusCategoryCompletionV2
import com.wellcome.main.wrapper.Delegate
import com.wellcome.main.wrapper.EntityWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityNotFoundException

interface OfferManagementService {
    fun getOffersV1(reviewing: Boolean, googleUid: String?, localityId: Long): OfferResponseV1
    fun getFullBlockOffersV1(reviewing: Boolean, googleUid: String?, localityId: Long, request: FullBlockOfferRequestV1): FullOfferResponseV1
    fun getFullBlockOffersV2(reviewing: Boolean, googleUid: String?, localityId: Long, request: FullBlockOfferRequestV1): FullOfferResponseV2
}

@Service
open class DefaultOfferManagementService @Autowired constructor(
    private val userService: UserService,
    private val timestampProvider: TimestampProvider,
    private val institutionService: InstitutionService,
    private val institutionCacheService: InstitutionCacheService,
    private val userInterceptorService: UserInterceptorService,
    private val applicationConfigService: ApplicationConfigService,
    private val workTimeInterceptorService: WorkTimeInterceptorService,
    private val institutionCategoryService: InstitutionCategoryService,
    private val storeReviewInterceptorService: StoreReviewInterceptorService
) : OfferManagementService {

    @Transactional(readOnly = true)
    override fun getOffersV1(reviewing: Boolean, googleUid: String?, localityId: Long): OfferResponseV1 {
        val user = googleUid?.let {
            userService.findByGoogleUid(googleUid)
                ?: throw EntityNotFoundException("User with googleUid: $googleUid is not found to database")
        }

        val userZonedDateTime = timestampProvider.getUserZonedDateTime()

        val workingOffers = institutionCacheService.findByLocality(localityId)
            .filter { it.offers.isNotEmpty() }
            .map(Institution::getWorkingOffers)
            .flatMap(List<InstitutionOffer>::toList)

        val offerCount = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.OFFER_RANGING_OFFER_SCREEN_COUNT).getLongValueNotNull()

        val workingOfferWrappers = workingOffers
            .map { EntityWrapper(it, mutableListOf()) }
            .let { if (user != null) userInterceptorService.handleSavedInstitutionsInsideOffers(user, it) else it }
            .let { workTimeInterceptorService.handleWorkTimeOffers(SearchInstitutionDays.NOW.name, userZonedDateTime, it) }
            .let { if (reviewing) storeReviewInterceptorService.handleForbiddenOffers(it) else it }
            .filter { wrapper ->
                (wrapper.delegates.first { it is Delegate.TimeDelegate } as Delegate.TimeDelegate).open
            }.let { wrappers ->
                return@let if (reviewing) wrappers.filter { wrapper ->
                    (wrapper.delegates.first { it is Delegate.StoreReviewDelegate } as Delegate.StoreReviewDelegate).allowed
                } else wrappers
            }

        return OfferResponseV1(
            pageTitle = Days.NOW.toAllTitle(),
            blockOffers = workingOfferWrappers.generateBlockOfferDtoV1List(getDay(SearchInstitutionDays.TODAY.name, userZonedDateTime)).map {
                it.copy(offers = it.offers.take(offerCount.toInt()))
            },
            workDayAttributes = generateOfferWorkDayAttributesDtoV1List(timestampProvider.getUserZonedDateTime())
        )
    }

    @Transactional(readOnly = true)
    override fun getFullBlockOffersV1(reviewing: Boolean, googleUid: String?, localityId: Long, request: FullBlockOfferRequestV1): FullOfferResponseV1 {
        val user = googleUid?.let {
            userService.findByGoogleUid(googleUid)
                ?: throw EntityNotFoundException("User with googleUid: $googleUid is not found to database")
        }

        val userZonedDateTime = timestampProvider.getUserZonedDateTime()

        val institutions = institutionCacheService.findByLocality(localityId)

        val institution = request.institutionId?.let(institutionService::findById)

        val category = request.categoryId?.let(institutionCategoryService::findById)

        val offers = mutableListOf<InstitutionOffer>()

        val chooseDay = request.day ?: SearchInstitutionDays.NOW.name

        val offerCount = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.OFFER_RANGING_OFFER_SCREEN_COUNT).getLongValueNotNull()

        if (request.day != null && request.type == null) {
            // при этом кейсе обрабатывается запрос после нажатия кнопки другой день на экране c акциями
            institutions
                .filter { it.offers.isNotEmpty() }
                .map(Institution::getWorkingOffers)
                .flatMap(List<InstitutionOffer>::toList)
                .let(offers::addAll)
        } else if (request.type != null && BlockType.valueOf(request.type) == BlockType.DYNAMIC_SEARCH_OFFERS) {
            // при этом кейсе обрабатывается запрос после нажатия кнопки смотркть все на экране с динамическим поиском
            institutions
                .filter { it.offers.isNotEmpty() }
                .map(Institution::getWorkingOffers)
                .flatMap(List<InstitutionOffer>::toList)
                .let(offers::addAll)
        } else if (request.type != null && BlockType.valueOf(request.type) == BlockType.FEED_OFFERS && request.day != null) {
            // при этом кейсе обрабатывается запрос после нажатия кнопки смотреть все в блоке рекоммендованых акций на экране feed после динамического поиска
            institutions
                .filterByInstitutionCategory(requireNotNull(category).categoryType)
                .filter { it.offers.isNotEmpty() }
                .map(Institution::getWorkingOffers)
                .flatMap(List<InstitutionOffer>::toList)
                .let(offers::addAll)
        } else if (request.type != null && BlockType.valueOf(request.type) == BlockType.FEED_OFFERS_INTERESTING && request.day != null) {
            // при этом кейсе обрабатывается запрос после нажатия кнопки смотреть все в блоке акций может быть интересно (акции по этой категории в заведениях другой категории) на экране feed после динамического поиска
            institutions
                .filterNotByInstitutionCategory(requireNotNull(category).categoryType)
                .filter { it.offers.isNotEmpty() }
                .map(Institution::getWorkingOffers)
                .flatMap(List<InstitutionOffer>::toList)
                .filter { it.offerType.checkOfferType(category.categoryType.getOfferTypes()) }
                .let(offers::addAll)
        } else if (request.type != null && BlockType.valueOf(request.type) == BlockType.INSTITUTION_PROFILE_OFFERS) {
            // при этом кейсе обрабатывается запрос после нажатия кнопки смотреть все в профиле заведения
            requireNotNull(institution).getWorkingOffers()
                .let(offers::addAll)
        } else if (request.type != null && request.day != null && BlockType.valueOf(request.type).isOfferType()) {
            // при этом кейсе обрабатывается запрос после нажатия кнопки смотреть все на экране после нажатия кнопки другой день
            institutions
                .filter { it.offers.isNotEmpty() }
                .map(Institution::getWorkingOffers)
                .flatMap(List<InstitutionOffer>::toList)
                .filter { it.offerType == BlockType.valueOf(request.type).toOfferType() }
                .let(offers::addAll)
        } else if (request.type != null && BlockType.valueOf(request.type).isOfferType()) {
            // при этом кейсе обрабатывается запрос после нажатия кнопки смотреть все на экране до нажатия кнопки другой день
            institutions
                .filter { it.offers.isNotEmpty() }
                .map(Institution::getWorkingOffers)
                .flatMap(List<InstitutionOffer>::toList)
                .filter { it.offerType == BlockType.valueOf(request.type).toOfferType() }
                .let(offers::addAll)
        } else throw Exception("Parameter case is not valid")

        val title = when {
            request.day != null && request.type == null -> {
                Days.valueOf(chooseDay).toAllTitle()
            }
            request.type != null && BlockType.valueOf(request.type) == BlockType.DYNAMIC_SEARCH_OFFERS -> {
                applicationConfigService
                    .getConfigValueByConfigType(ApplicationConfigType.OTHER_OFFERS_TITLE_ALL)
                    .getStringValueNotNull()
            }
            request.type != null && BlockType.valueOf(request.type) == BlockType.FEED_OFFERS -> {
                applicationConfigService
                    .getConfigValueByConfigType(ApplicationConfigType.INSTITUTION_OFFERS_TITLE)
                    .getStringValueNotNull()
                    .plusCategoryCompletionV2(requireNotNull(category).categoryType)
                    .plus(Days.valueOf(chooseDay).getEnding())
            }
            request.type != null && BlockType.valueOf(request.type) == BlockType.FEED_OFFERS_INTERESTING -> {
                applicationConfigService
                    .getConfigValueByConfigType(ApplicationConfigType.OTHER_OFFERS_TITLE)
                    .getStringValueNotNull()
            }
            request.type != null && BlockType.valueOf(request.type) == BlockType.INSTITUTION_PROFILE_OFFERS -> {
                applicationConfigService
                    .getConfigValueByConfigType(ApplicationConfigType.INSTITUTION_OFFERS_TITLE)
                    .getStringValueNotNull()
                    .plus(" в ${requireNotNull(institution).name}")
            }
            request.type != null && BlockType.valueOf(request.type).isOfferType() -> {
                BlockType.valueOf(request.type).toOfferType().toAllOfferTitle(Days.valueOf(chooseDay))
            }
            else -> ""
        }

        val filteredOfferWrappers = offers
            .map { EntityWrapper(it) }
            .let { if (user != null) userInterceptorService.handleSavedInstitutionsInsideOffers(user, it) else it }
            .let { if (institution == null) workTimeInterceptorService.handleWorkTimeOffers(chooseDay, userZonedDateTime, it) else it }
            .let { if (reviewing) storeReviewInterceptorService.handleForbiddenOffers(it) else it }
            .let { if (institution == null) it.filter { wrapper -> (wrapper.delegates.first { delegate -> delegate is Delegate.TimeDelegate } as Delegate.TimeDelegate).open } else it }
            .let { wrappers ->
                return@let if (reviewing) wrappers.filter { wrapper ->
                    (wrapper.delegates.first { it is Delegate.StoreReviewDelegate } as Delegate.StoreReviewDelegate).allowed
                } else wrappers
            }

        return if (request.day != null && request.type == null) {
            FullOfferResponseV1(
                title = title,
                blockOffers = filteredOfferWrappers.generateBlockOfferDtoV1List(getDay(chooseDay, userZonedDateTime)).map {
                    it.copy(offers = it.offers.take(offerCount.toInt()))
                }
            )
        } else FullOfferResponseV1(
            title = title,
            blockOffers = BlockOffersDtoV1(
                title = "",
                blockType = requireNotNull(request.type),
                offers = filteredOfferWrappers.generateOfferDtoV1List(getDay(chooseDay, userZonedDateTime)),
                showAll = false
            ).let(::listOf)
        )
    }

    @Transactional(readOnly = true)
    override fun getFullBlockOffersV2(reviewing: Boolean, googleUid: String?, localityId: Long, request: FullBlockOfferRequestV1): FullOfferResponseV2 {
        val user = googleUid?.let {
            userService.findByGoogleUid(googleUid)
                ?: throw EntityNotFoundException("User with googleUid: $googleUid is not found to database")
        }

        val userZonedDateTime = timestampProvider.getUserZonedDateTime()

        val institutions = institutionCacheService.findByLocality(localityId)

        val institution = request.institutionId?.let(institutionService::findById)

        val category = request.categoryId?.let(institutionCategoryService::findById)

        val offers = mutableListOf<InstitutionOffer>()

        val chooseDay = request.day ?: SearchInstitutionDays.NOW.name

        val offerCount = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.OFFER_RANGING_OFFER_SCREEN_COUNT).getLongValueNotNull()

        if (request.day != null && request.type == null) {
            institutions
                .filter { it.offers.isNotEmpty() }
                .map(Institution::getWorkingOffers)
                .flatMap(List<InstitutionOffer>::toList)
                .let(offers::addAll)
        } else if (request.type != null && BlockType.valueOf(request.type) == BlockType.DYNAMIC_SEARCH_OFFERS) {
            institutions
                .filter { it.offers.isNotEmpty() }
                .map(Institution::getWorkingOffers)
                .flatMap(List<InstitutionOffer>::toList)
                .let(offers::addAll)
        } else if (request.type != null && BlockType.valueOf(request.type) == BlockType.FEED_OFFERS && request.day != null) {
            institutions
                .filterByInstitutionCategory(requireNotNull(category).categoryType)
                .filter { it.offers.isNotEmpty() }
                .map(Institution::getWorkingOffers)
                .flatMap(List<InstitutionOffer>::toList)
                .let(offers::addAll)
        } else if (request.type != null && BlockType.valueOf(request.type) == BlockType.FEED_OFFERS_INTERESTING && request.day != null) {
            institutions
                .filterNotByInstitutionCategory(requireNotNull(category).categoryType)
                .filter { it.offers.isNotEmpty() }
                .map(Institution::getWorkingOffers)
                .flatMap(List<InstitutionOffer>::toList)
                .filter { it.offerType.checkOfferType(category.categoryType.getOfferTypes()) }
                .let(offers::addAll)
        } else if (request.type != null && BlockType.valueOf(request.type) == BlockType.INSTITUTION_PROFILE_OFFERS) {
            requireNotNull(institution).getWorkingOffers()
                .let(offers::addAll)
        } else if (request.type != null && request.day != null && BlockType.valueOf(request.type).isOfferType()) {
            institutions
                .filter { it.offers.isNotEmpty() }
                .map(Institution::getWorkingOffers)
                .flatMap(List<InstitutionOffer>::toList)
                .filter { it.offerType == BlockType.valueOf(request.type).toOfferType() }
                .let(offers::addAll)
        } else if (request.type != null && BlockType.valueOf(request.type).isOfferType()) {
            institutions
                .filter { it.offers.isNotEmpty() }
                .map(Institution::getWorkingOffers)
                .flatMap(List<InstitutionOffer>::toList)
                .filter { it.offerType == BlockType.valueOf(request.type).toOfferType() }
                .let(offers::addAll)
        } else throw Exception("Parameter case is not valid")

        val title = when {
            request.day != null && request.type == null -> {
                Days.valueOf(chooseDay).toAllTitle()
            }
            request.type != null && BlockType.valueOf(request.type) == BlockType.DYNAMIC_SEARCH_OFFERS -> {
                applicationConfigService
                    .getConfigValueByConfigType(ApplicationConfigType.OTHER_OFFERS_TITLE_ALL)
                    .getStringValueNotNull()
            }
            request.type != null && BlockType.valueOf(request.type) == BlockType.FEED_OFFERS -> {
                applicationConfigService
                    .getConfigValueByConfigType(ApplicationConfigType.INSTITUTION_OFFERS_TITLE)
                    .getStringValueNotNull()
                    .plusCategoryCompletionV2(requireNotNull(category).categoryType)
                    .plus(Days.valueOf(chooseDay).getEnding())
            }
            request.type != null && BlockType.valueOf(request.type) == BlockType.FEED_OFFERS_INTERESTING -> {
                applicationConfigService
                    .getConfigValueByConfigType(ApplicationConfigType.OTHER_OFFERS_TITLE)
                    .getStringValueNotNull()
            }
            request.type != null && BlockType.valueOf(request.type) == BlockType.INSTITUTION_PROFILE_OFFERS -> {
                applicationConfigService
                    .getConfigValueByConfigType(ApplicationConfigType.INSTITUTION_OFFERS_TITLE)
                    .getStringValueNotNull()
                    .plus(" в ${requireNotNull(institution).name}")
            }
            request.type != null && BlockType.valueOf(request.type).isOfferType() -> {
                BlockType.valueOf(request.type).toOfferType().toAllOfferTitle(Days.valueOf(chooseDay))
            }
            else -> ""
        }

        val filteredOfferWrappers = offers
            .map { EntityWrapper(it) }
            .let { if (user != null) userInterceptorService.handleSavedInstitutionsInsideOffers(user, it) else it }
            .let { if (institution == null) workTimeInterceptorService.handleWorkTimeOffers(chooseDay, userZonedDateTime, it) else it }
            .let { if (reviewing) storeReviewInterceptorService.handleForbiddenOffers(it) else it }
            .let { if (institution == null) it.filter { wrapper -> (wrapper.delegates.first { delegate -> delegate is Delegate.TimeDelegate } as Delegate.TimeDelegate).open } else it }
            .let { wrappers ->
                return@let if (reviewing) wrappers.filter { wrapper ->
                    (wrapper.delegates.first { it is Delegate.StoreReviewDelegate } as Delegate.StoreReviewDelegate).allowed
                } else wrappers
            }

        return if (request.day != null && request.type == null) {
            FullOfferResponseV2(
                title = title,
                blockOffers = filteredOfferWrappers.generateBlockOfferDtoV2List(getDay(chooseDay, userZonedDateTime)).map {
                    it.copy(offers = it.offers.take(offerCount.toInt()))
                }
            )
        } else FullOfferResponseV2(
            title = title,
            blockOffers = BlockOffersDtoV2(
                title = "",
                blockType = requireNotNull(request.type),
                offers = filteredOfferWrappers.generateOfferDtoV2List(getDay(chooseDay, userZonedDateTime)),
                showAll = false
            ).let(::listOf)
        )
    }
}