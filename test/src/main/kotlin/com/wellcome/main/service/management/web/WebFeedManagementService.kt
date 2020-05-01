package com.wellcome.main.service.management.web

import com.wellcome.main.component.TimestampProvider
import com.wellcome.main.dto.web.response.FeedResultStepOneResponse
import com.wellcome.main.dto.web.response.FeedResultStepThreeResponse
import com.wellcome.main.dto.web.response.FeedResultStepTwoResponse
import com.wellcome.main.entity.ApplicationConfigType
import com.wellcome.main.entity.institution.Institution
import com.wellcome.main.entity.institution.InstitutionEvent
import com.wellcome.main.entity.institution.InstitutionOffer
import com.wellcome.main.service.cache.InstitutionCacheService
import com.wellcome.main.service.extentions.generators.web.common.generateEventDtoList
import com.wellcome.main.service.extentions.generators.web.common.generateInstitutionDtoList
import com.wellcome.main.service.extentions.generators.web.common.generateOfferDtoList
import com.wellcome.main.service.extentions.management.filterByInstitutionCategory
import com.wellcome.main.service.extentions.management.filterNotByInstitutionCategory
import com.wellcome.main.service.facade.ApplicationConfigService
import com.wellcome.main.service.facade.institution.InstitutionCategoryService
import com.wellcome.main.service.facade.user.UserService
import com.wellcome.main.service.interceptor.UserInterceptorService
import com.wellcome.main.service.interceptor.WorkTimeInterceptorService
import com.wellcome.main.util.enumerators.SearchInstitutionDays
import com.wellcome.main.util.functions.getDay
import com.wellcome.main.wrapper.Delegate
import com.wellcome.main.wrapper.EntityWrapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityNotFoundException

interface WebFeedManagementService {
    fun feedStepOne(categoryId: Long, day: String, localityId: Long, googleUid: String?): FeedResultStepOneResponse
    fun feedStepTwo(categoryId: Long, day: String, localityId: Long, googleUid: String?): FeedResultStepTwoResponse
    fun feedStemThree(categoryId: Long, day: String, localityId: Long, googleUid: String?): FeedResultStepThreeResponse
}

@Service
open class WebDefaultFeedManagementService constructor(
    private val userService: UserService,
    private val timestampProvider: TimestampProvider,
    private val userInterceptorService: UserInterceptorService,
    private val institutionCacheService: InstitutionCacheService,
    private val applicationConfigService: ApplicationConfigService,
    private val workTimeInterceptorService: WorkTimeInterceptorService,
    private val institutionCategoryService: InstitutionCategoryService
) : WebFeedManagementService {

    @Transactional(readOnly = true)
    override fun feedStepOne(categoryId: Long, day: String, localityId: Long, googleUid: String?): FeedResultStepOneResponse {
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
            .filter { wrapper -> (wrapper.delegates.first { it is Delegate.TimeDelegate } as Delegate.TimeDelegate).open }
            .sortedByDescending { it.entity.rating }
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
            .filter { wrapper -> (wrapper.delegates.first { it is Delegate.TimeDelegate } as Delegate.TimeDelegate).open }
            .shuffled()

        val categoryInstitutionEventWrappers =
            if (day == SearchInstitutionDays.BIRTHDAY.name) emptyList()
            else categoryInstitutions
                .filter { it.events.isNotEmpty() }
                .map(Institution::getWorkingEvents)
                .flatMap(List<InstitutionEvent>::toList)
                .map { EntityWrapper(it, mutableListOf()) }
                .let { if (user != null) userInterceptorService.handleSavedInstitutionsInsideEvent(user, it) else it }
                .let { workTimeInterceptorService.handleWorkTimeEvents(day, userZonedDateTime, it) }
                .filter { wrapper -> (wrapper.delegates.first { it is Delegate.TimeDelegate } as Delegate.TimeDelegate).open }

        val dayOfWeek = getDay(day, userZonedDateTime)

        return FeedResultStepOneResponse(
            offers = recommendedOfferWrappers.generateOfferDtoList(dayOfWeek).take(offerCount.toInt()),
            categoryInstitutionEvents = categoryInstitutionEventWrappers.generateEventDtoList(dayOfWeek),
            recommendedInstitutions = recommendedInstitutionWrappers.generateInstitutionDtoList(dayOfWeek)
        )
    }

    @Transactional(readOnly = true)
    override fun feedStepTwo(categoryId: Long, day: String, localityId: Long, googleUid: String?): FeedResultStepTwoResponse {
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
            .filter { wrapper -> (wrapper.delegates.first { it is Delegate.TimeDelegate } as Delegate.TimeDelegate).open }
            .sortedByDescending { it.entity.rating }

        val dayOfWeek = getDay(day, userZonedDateTime)

        return FeedResultStepTwoResponse(
            allInstitutions = categoryInstitutionWrappers.generateInstitutionDtoList(dayOfWeek)
        )
    }

    @Transactional(readOnly = true)
    override fun feedStemThree(categoryId: Long, day: String, localityId: Long, googleUid: String?): FeedResultStepThreeResponse {
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
            .filter { wrapper -> (wrapper.delegates.first { it is Delegate.TimeDelegate } as Delegate.TimeDelegate).open }
            .shuffled()

        val dayOfWeek = getDay(day, userZonedDateTime)

        return FeedResultStepThreeResponse(
            otherOffers = otherOfferWrappers.generateOfferDtoList(dayOfWeek).take(offerCount.toInt())
        )
    }

}