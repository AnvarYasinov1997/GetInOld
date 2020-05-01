package com.wellcome.main.service.management.web

import com.wellcome.main.component.TimestampProvider
import com.wellcome.main.dto.api.newDto.Days
import com.wellcome.main.dto.api.newDto.common.v1.BlockType
import com.wellcome.main.dto.web.common.BlockOffersDto
import com.wellcome.main.dto.web.request.AllOfferRequest
import com.wellcome.main.dto.web.response.AllOfferResponse
import com.wellcome.main.entity.ApplicationConfigType
import com.wellcome.main.entity.institution.Institution
import com.wellcome.main.entity.institution.InstitutionOffer
import com.wellcome.main.service.cache.InstitutionCacheService
import com.wellcome.main.service.extentions.generators.web.common.generateBlockOfferDtoList
import com.wellcome.main.service.extentions.generators.web.common.generateOfferDtoList
import com.wellcome.main.service.extentions.management.filterByInstitutionCategory
import com.wellcome.main.service.extentions.management.filterNotByInstitutionCategory
import com.wellcome.main.service.facade.ApplicationConfigService
import com.wellcome.main.service.facade.institution.InstitutionCategoryService
import com.wellcome.main.service.facade.institution.InstitutionOfferService
import com.wellcome.main.service.facade.institution.InstitutionService
import com.wellcome.main.service.facade.user.UserService
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

interface WebOfferManagementService {
    fun getAll(googleUid: String?,
               localityId: Long,
               request: AllOfferRequest): AllOfferResponse
}

@Service
open class DefaultWebOfferManagementService @Autowired constructor(
    private val userService: UserService,
    private val timestampProvider: TimestampProvider,
    private val institutionService: InstitutionService,
    private val userInterceptorService: UserInterceptorService,
    private val institutionCacheService: InstitutionCacheService,
    private val applicationConfigService: ApplicationConfigService,
    private val workTimeInterceptorService: WorkTimeInterceptorService,
    private val institutionCategoryService: InstitutionCategoryService
) : WebOfferManagementService {

    @Transactional(readOnly = true)
    override fun getAll(googleUid: String?,
                        localityId: Long,
                        request: AllOfferRequest): AllOfferResponse {
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
            .let { if (institution == null) it.filter { wrapper -> (wrapper.delegates.first { delegate -> delegate is Delegate.TimeDelegate } as Delegate.TimeDelegate).open } else it }

        return if (request.day != null && request.type == null) {
            AllOfferResponse(
                title = title,
                blockOffers = filteredOfferWrappers.generateBlockOfferDtoList(getDay(chooseDay, userZonedDateTime)).map {
                    it.copy(offers = it.offers.take(offerCount.toInt()))
                }
            )
        } else AllOfferResponse(
            title = title,
            blockOffers = BlockOffersDto(
                title = "",
                blockType = requireNotNull(request.type),
                offers = filteredOfferWrappers.generateOfferDtoList(getDay(chooseDay, userZonedDateTime)),
                showAll = false
            ).let(::listOf)
        )
    }

}