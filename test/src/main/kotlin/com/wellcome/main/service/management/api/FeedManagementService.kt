package com.wellcome.main.service.management.api

import com.wellcome.main.component.TimestampProvider
import com.wellcome.main.dto.api.newDto.response.v1.FeedResultStepOneResponseV1
import com.wellcome.main.dto.api.newDto.response.v1.FeedResultStepThreeResponseV1
import com.wellcome.main.dto.api.newDto.response.v1.FeedResultStepTwoResponseV1
import com.wellcome.main.entity.ApplicationConfigType
import com.wellcome.main.entity.institution.Institution
import com.wellcome.main.entity.institution.InstitutionEvent
import com.wellcome.main.entity.institution.InstitutionOffer
import com.wellcome.main.service.cache.InstitutionCacheService
import com.wellcome.main.service.extentions.generators.api.common.generateEventDtoV1List
import com.wellcome.main.service.extentions.generators.api.common.generateInstitutionDtoV1List
import com.wellcome.main.service.extentions.generators.api.common.v2.generateOfferDtoV2List
import com.wellcome.main.service.extentions.management.filterByInstitutionCategory
import com.wellcome.main.service.extentions.management.filterNotByInstitutionCategory
import com.wellcome.main.service.facade.ApplicationConfigService
import com.wellcome.main.service.facade.institution.InstitutionCategoryService
import com.wellcome.main.service.facade.user.UserService
import com.wellcome.main.service.interceptor.StoreReviewInterceptorService
import com.wellcome.main.service.interceptor.UserInterceptorService
import com.wellcome.main.service.interceptor.WorkTimeInterceptorService
import com.wellcome.main.util.enumerators.SearchInstitutionDays
import com.wellcome.main.util.functions.getDay
import com.wellcome.main.wrapper.Delegate
import com.wellcome.main.wrapper.EntityWrapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityNotFoundException

interface FeedManagementService {
    fun feedStepOne(reviewing: Boolean, categoryId: Long, day: String, localityId: Long, googleUid: String?): FeedResultStepOneResponseV1
    fun feedStepTwo(reviewing: Boolean, categoryId: Long, day: String, localityId: Long, googleUid: String?): FeedResultStepTwoResponseV1
    fun feedStemThree(reviewing: Boolean, categoryId: Long, day: String, localityId: Long, googleUid: String?): FeedResultStepThreeResponseV1
}

@Service
open class DefaultFeedManagementService constructor(
    private val userService: UserService,
    private val timestampProvider: TimestampProvider,
    private val userInterceptorService: UserInterceptorService,
    private val institutionCacheService: InstitutionCacheService,
    private val applicationConfigService: ApplicationConfigService,
    private val workTimeInterceptorService: WorkTimeInterceptorService,
    private val institutionCategoryService: InstitutionCategoryService,
    private val storeReviewInterceptorService: StoreReviewInterceptorService
) : FeedManagementService {

    @Transactional(readOnly = true)
    override fun feedStepOne(reviewing: Boolean, categoryId: Long, day: String, localityId: Long, googleUid: String?): FeedResultStepOneResponseV1 {
        val user = googleUid?.let {
            userService.findByGoogleUid(googleUid)
                ?: throw EntityNotFoundException("User with googleUid: $googleUid is not found to database")
        }
        val userZonedDateTime = timestampProvider.getUserZonedDateTime()

        val category = institutionCategoryService.findById(categoryId)

        val institutions = institutionCacheService.findByLocality(localityId)

        val categoryInstitutions = institutions
            .filterByInstitutionCategory(category.categoryType)

        val institutionCount = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.INSTITUTION_RANGING_SEARCH_SCREEN_COUNT).getLongValueNotNull()
        val offerCount = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.OFFER_RANGING_SEARCH_SCREEN_COUNT).getLongValueNotNull()

        val recommendedInstitutionWrappers = categoryInstitutions
            .map { EntityWrapper(it) }
            .let { if (user != null) userInterceptorService.handleSavedInstitutions(user, it) else it }
            .let { workTimeInterceptorService.handleWorkTimeInstitutions(day, userZonedDateTime, it) }
            .let { if (reviewing) storeReviewInterceptorService.handleForbiddenInstitutions(it) else it }
            .filter { wrapper -> (wrapper.delegates.first { it is Delegate.TimeDelegate } as Delegate.TimeDelegate).open }
            .let { wrappers ->
                return@let if (reviewing) wrappers.filter { wrapper ->
                    (wrapper.delegates.first { it is Delegate.StoreReviewDelegate } as Delegate.StoreReviewDelegate).allowed
                } else wrappers
            }.sortedByDescending { it.entity.rating }
            .take(institutionCount.toInt())
            .shuffled()

        val recommendedOfferWrappers = categoryInstitutions
            .filter { it.offers.isNotEmpty() }
            .map(Institution::getWorkingOffers)
            .flatMap(List<InstitutionOffer>::toList)
            .sortedByDescending(InstitutionOffer::getInstitutionRatingNotNull)
            .map { EntityWrapper(it, mutableListOf()) }
            .let { if (user != null) userInterceptorService.handleSavedInstitutionsInsideOffers(user, it) else it }
            .let { workTimeInterceptorService.handleWorkTimeOffers(day, userZonedDateTime, it) }
            .let { if (reviewing) storeReviewInterceptorService.handleForbiddenOffers(it) else it }
            .filter { wrapper -> (wrapper.delegates.first { it is Delegate.TimeDelegate } as Delegate.TimeDelegate).open }
            .let { wrappers ->
                return@let if (reviewing) wrappers.filter { wrapper ->
                    (wrapper.delegates.first { it is Delegate.StoreReviewDelegate } as Delegate.StoreReviewDelegate).allowed
                } else wrappers
            }.shuffled()

        val categoryInstitutionEventWrappers =
            if (day == SearchInstitutionDays.BIRTHDAY.name) emptyList()
            else categoryInstitutions
                .filter { it.events.isNotEmpty() }
                .map(Institution::getWorkingEvents)
                .flatMap(List<InstitutionEvent>::toList)
                .map { EntityWrapper(it, mutableListOf()) }
                .let { if (user != null) userInterceptorService.handleSavedInstitutionsInsideEvent(user, it) else it }
                .let { workTimeInterceptorService.handleWorkTimeEvents(day, userZonedDateTime, it) }
                .let { if (reviewing) storeReviewInterceptorService.handleForbiddenEvents(it) else it }
                .filter { wrapper -> (wrapper.delegates.first { it is Delegate.TimeDelegate } as Delegate.TimeDelegate).open }
                .let { wrappers ->
                    return@let if (reviewing) wrappers.filter { wrapper ->
                        (wrapper.delegates.first { it is Delegate.StoreReviewDelegate } as Delegate.StoreReviewDelegate).allowed
                    } else wrappers
                }

        val dayOfWeek = getDay(day, userZonedDateTime)

        return FeedResultStepOneResponseV1(
            offers = recommendedOfferWrappers.generateOfferDtoV2List(dayOfWeek).take(offerCount.toInt()),
            categoryInstitutionEvents = categoryInstitutionEventWrappers.generateEventDtoV1List(dayOfWeek),
            recommendedInstitutions = recommendedInstitutionWrappers.generateInstitutionDtoV1List(dayOfWeek)
        )
    }

    @Transactional(readOnly = true)
    override fun feedStepTwo(reviewing: Boolean, categoryId: Long, day: String, localityId: Long, googleUid: String?): FeedResultStepTwoResponseV1 {
        val user = googleUid?.let {
            userService.findByGoogleUid(googleUid)
                ?: throw EntityNotFoundException("User with googleUid: $googleUid is not found to database")
        }
        val userZonedDateTime = timestampProvider.getUserZonedDateTime()

        val category = institutionCategoryService.findById(categoryId)

        val institutions = institutionCacheService.findByLocality(localityId)

        val categoryInstitutions = institutions
            .filterByInstitutionCategory(category.categoryType)

        val categoryInstitutionWrappers = categoryInstitutions
            .map { EntityWrapper(it) }
            .let { if (user != null) userInterceptorService.handleSavedInstitutions(user, it) else it }
            .let { workTimeInterceptorService.handleWorkTimeInstitutions(day, userZonedDateTime, it) }
            .let { if (reviewing) storeReviewInterceptorService.handleForbiddenInstitutions(it) else it }
            .filter { wrapper -> (wrapper.delegates.first { it is Delegate.TimeDelegate } as Delegate.TimeDelegate).open }
            .let { wrappers ->
                return@let if (reviewing) wrappers.filter { wrapper ->
                    (wrapper.delegates.first { it is Delegate.StoreReviewDelegate } as Delegate.StoreReviewDelegate).allowed
                } else wrappers
            }.sortedByDescending { it.entity.rating }

        val dayOfWeek = getDay(day, userZonedDateTime)

        return FeedResultStepTwoResponseV1(
            allInstitutions = categoryInstitutionWrappers.generateInstitutionDtoV1List(dayOfWeek)
        )
    }

    @Transactional(readOnly = true)
    override fun feedStemThree(reviewing: Boolean, categoryId: Long, day: String, localityId: Long, googleUid: String?): FeedResultStepThreeResponseV1 {
        val user = googleUid?.let {
            userService.findByGoogleUid(googleUid)
                ?: throw EntityNotFoundException("User with googleUid: $googleUid is not found to database")
        }
        val userZonedDateTime = timestampProvider.getUserZonedDateTime()

        val category = institutionCategoryService.findById(categoryId)

        val institutions = institutionCacheService.findByLocality(localityId)

        val otherInstitutions = institutions
            .filterNotByInstitutionCategory(category.categoryType)

        val offerCount = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.OFFER_RANGING_SEARCH_SCREEN_COUNT).getLongValueNotNull()

        val otherOfferWrappers = otherInstitutions
            .filter { it.offers.isNotEmpty() }
            .map(Institution::getWorkingOffers)
            .flatMap(List<InstitutionOffer>::toList)
            .filter { it.offerType.checkOfferType(category.categoryType.getOfferTypes()) }
            .map { EntityWrapper(it, mutableListOf()) }
            .let { if (user != null) userInterceptorService.handleSavedInstitutionsInsideOffers(user, it) else it }
            .let { workTimeInterceptorService.handleWorkTimeOffers(day, userZonedDateTime, it) }
            .let { if (reviewing) storeReviewInterceptorService.handleForbiddenOffers(it) else it }
            .filter { wrapper -> (wrapper.delegates.first { it is Delegate.TimeDelegate } as Delegate.TimeDelegate).open }
            .let { wrappers ->
                return@let if (reviewing) wrappers.filter { wrapper ->
                    (wrapper.delegates.first { it is Delegate.StoreReviewDelegate } as Delegate.StoreReviewDelegate).allowed
                } else wrappers
            }.shuffled()

        val dayOfWeek = getDay(day, userZonedDateTime)

        return FeedResultStepThreeResponseV1(
            otherOffers = otherOfferWrappers.generateOfferDtoV2List(dayOfWeek).take(offerCount.toInt())
        )
    }
}