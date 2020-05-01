package com.wellcome.main.service.management.api

import com.wellcome.main.component.PersistentContextProvider
import com.wellcome.main.component.TimestampProvider
import com.wellcome.main.dto.api.newDto.common.v1.BlockInstitutionsDtoV1
import com.wellcome.main.dto.api.newDto.response.v1.ClosestInstitutionResponseV1
import com.wellcome.main.dto.api.newDto.response.v1.InstitutionMapResponseV1
import com.wellcome.main.dto.api.newDto.response.v1.ProfileResponseV1
import com.wellcome.main.dto.api.newDto.response.v2.ProfileResponseV2
import com.wellcome.main.dto.api.newDto.response.v3.ProfileResponseV3
import com.wellcome.main.entity.ApplicationConfigType
import com.wellcome.main.service.cache.InstitutionCacheService
import com.wellcome.main.service.extentions.generators.api.common.generateInstitutionDtoV1List
import com.wellcome.main.service.extentions.generators.api.v1.generateInstitutionMapDtoV1List
import com.wellcome.main.service.extentions.generators.api.v1.generateProfileResponseV1
import com.wellcome.main.service.extentions.generators.api.v2.generateProfileResponseV2
import com.wellcome.main.service.extentions.generators.api.v3.generateProfileResponseV3
import com.wellcome.main.service.extentions.management.filterByInstitutionCategory
import com.wellcome.main.service.facade.ApplicationConfigService
import com.wellcome.main.service.facade.institution.InstitutionCategoryService
import com.wellcome.main.service.facade.user.UserService
import com.wellcome.main.service.interceptor.StoreReviewInterceptorService
import com.wellcome.main.service.interceptor.UserInterceptorService
import com.wellcome.main.service.interceptor.WorkTimeInterceptorService
import com.wellcome.main.util.enumerators.SearchInstitutionDays
import com.wellcome.main.util.functions.calculateDistance
import com.wellcome.main.util.functions.distanceBetweenLatLon
import com.wellcome.main.util.functions.getDay
import com.wellcome.main.wrapper.Delegate
import com.wellcome.main.wrapper.EntityWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityNotFoundException

interface InstitutionManagementService {
    fun getForMap(localityId: Long, reviewing: Boolean): InstitutionMapResponseV1
    fun getClosestV1(localityId: Long, lat: Double, lon: Double, categoryId: Long, day: String, googleUid: String?, reviewing: Boolean): ClosestInstitutionResponseV1
    fun getProfileV1(institutionId: Long, googleUid: String?): ProfileResponseV1
    fun getProfileV2(institutionId: Long, googleUid: String?): ProfileResponseV2
    fun getProfileV3(institutionId: Long, googleUid: String?): ProfileResponseV3
}

@Service
open class DefaultInstitutionManagementService @Autowired constructor(
    private val userService: UserService,
    private val timestampProvider: TimestampProvider,
    private val userInterceptorService: UserInterceptorService,
    private val institutionCacheService: InstitutionCacheService,
    private val applicationConfigService: ApplicationConfigService,
    private val persistentContextProvider: PersistentContextProvider,
    private val workTimeInterceptorService: WorkTimeInterceptorService,
    private val institutionCategoryService: InstitutionCategoryService,
    private val storeReviewInterceptorService: StoreReviewInterceptorService
) : InstitutionManagementService {

    @Transactional(readOnly = true)
    override fun getForMap(localityId: Long, reviewing: Boolean): InstitutionMapResponseV1 {
        val userZonedDateTime = timestampProvider.getUserZonedDateTime()
        val institutions = institutionCacheService.findByLocality(localityId)
            .map { EntityWrapper(it) }
            .let { workTimeInterceptorService.handleWorkTimeInstitutions(SearchInstitutionDays.NOW.name, userZonedDateTime, it) }
            .let { if (reviewing) storeReviewInterceptorService.handleForbiddenInstitutions(it) else it }
            .let { wrappers ->
                return@let if (reviewing) wrappers.filter { wrapper ->
                    (wrapper.delegates.first { it is Delegate.StoreReviewDelegate } as Delegate.StoreReviewDelegate).allowed
                } else wrappers
            }

        return InstitutionMapResponseV1(institutions.generateInstitutionMapDtoV1List(getDay(SearchInstitutionDays.NOW.name, userZonedDateTime)))
    }

    @Transactional(readOnly = true)
    override fun getClosestV1(localityId: Long, lat: Double, lon: Double, categoryId: Long, day: String, googleUid: String?, reviewing: Boolean): ClosestInstitutionResponseV1 {
        val user = googleUid?.let {
            userService.findByGoogleUid(googleUid)
                ?: throw EntityNotFoundException("User with googleUid: $googleUid is not found to database")
        }
        val userZonedDateTime = timestampProvider.getUserZonedDateTime()

        val institutionCount =
            applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.INSTITUTION_RANGING_SEARCH_SCREEN_COUNT).getLongValueNotNull()

        val category = institutionCategoryService.findById(categoryId)

        val institutionCategoryWrappers = institutionCacheService.findByLocality(localityId)
            .filterByInstitutionCategory(category.categoryType)
            .map { EntityWrapper(it) }

        val title = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.CLOSEST_INSTITUTION_TITLE).getStringValueNotNull()

        val closestInstitutionWrappers = institutionCategoryWrappers
            .let { if (user != null) userInterceptorService.handleSavedInstitutions(user, it) else it }
            .let { workTimeInterceptorService.handleWorkTimeInstitutions(day, userZonedDateTime, it) }
            .let { if (reviewing) storeReviewInterceptorService.handleForbiddenInstitutions(it) else it }
            .filter { wrapper -> (wrapper.delegates.first { it is Delegate.TimeDelegate } as Delegate.TimeDelegate).open }
            .let { wrappers ->
                return@let if (reviewing) wrappers.filter { wrapper ->
                    (wrapper.delegates.first { it is Delegate.StoreReviewDelegate } as Delegate.StoreReviewDelegate).allowed
                } else wrappers
            }.map { wrapper ->
                distanceBetweenLatLon(lat, lon,
                    wrapper.entity.locationAttributes.lat,
                    wrapper.entity.locationAttributes.lon)
                    .calculateDistance()
                    .toInt().let { Pair(requireNotNull(wrapper.entity.id), it) }
            }
            .sortedBy(Pair<Long, Int>::second)
            .map { pair ->
                institutionCategoryWrappers.first {
                    it.entity.id == pair.first
                }
            }.take(institutionCount.toInt())

        return ClosestInstitutionResponseV1(BlockInstitutionsDtoV1(
            title=title,
            institutions = closestInstitutionWrappers
                .generateInstitutionDtoV1List(getDay(SearchInstitutionDays.NOW.name, userZonedDateTime)))
        )
    }

    @Transactional(readOnly = true)
    override fun getProfileV1(institutionId: Long, googleUid: String?): ProfileResponseV1 {
        val user = googleUid?.let {
            userService.findByGoogleUid(googleUid)
                ?: throw EntityNotFoundException("User with googleUid: $googleUid is not found to database")
        }
        val userZonedDateTime = timestampProvider.getUserZonedDateTime()

        persistentContextProvider.refreshCache()

        val institution = institutionCacheService.findById(institutionId)

        val reviewCount = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.INSTITUTION_PROFILE_REVIEWS_COUNT).getLongValueNotNull()
        val offerCount = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.INSTITUTION_PROFILE_OFFERS_COUNT).getLongValueNotNull()

        val institutionWrapper = mutableListOf(institution)
            .map { EntityWrapper(it, mutableListOf()) }
            .let { if (user != null) userInterceptorService.handleSavedInstitutions(user, it) else it }
            .let { workTimeInterceptorService.handleWorkTimeInstitutions(SearchInstitutionDays.NOW.name, userZonedDateTime, it) }
            .map { wrapper ->
                wrapper.entity.reviews = institution.reviews.sortedByDescending { it.feedback.length }.toMutableList()
                return@map wrapper
            }
            .first()

        return institutionWrapper.generateProfileResponseV1(
            dayOfWeek = getDay(SearchInstitutionDays.NOW.name, userZonedDateTime),
            partnerPictures = emptyList(),
            userId = user?.id,
            reviewsCount = reviewCount,
            offersCount = offerCount
        )
    }

    @Transactional(readOnly = true)
    override fun getProfileV2(institutionId: Long, googleUid: String?): ProfileResponseV2 {
        val user = googleUid?.let {
            userService.findByGoogleUid(googleUid)
                ?: throw EntityNotFoundException("User with googleUid: $googleUid is not found to database")
        }
        val userZonedDateTime = timestampProvider.getUserZonedDateTime()

        persistentContextProvider.refreshCache()

        val institution = institutionCacheService.findById(institutionId)

        val reviewCount = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.INSTITUTION_PROFILE_REVIEWS_COUNT).getLongValueNotNull()
        val offerCount = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.INSTITUTION_PROFILE_OFFERS_COUNT).getLongValueNotNull()

        val institutionWrapper = mutableListOf(institution)
            .map { EntityWrapper(it, mutableListOf()) }
            .let { if (user != null) userInterceptorService.handleSavedInstitutions(user, it) else it }
            .let { workTimeInterceptorService.handleWorkTimeInstitutions(SearchInstitutionDays.NOW.name, userZonedDateTime, it) }
            .map { wrapper ->
                wrapper.entity.reviews = institution.reviews.sortedByDescending { it.feedback.length }.toMutableList()
                return@map wrapper
            }
            .first()

        return institutionWrapper.generateProfileResponseV2(
            dayOfWeek = getDay(SearchInstitutionDays.NOW.name, userZonedDateTime),
            partnerPictures = emptyList(),
            userId = user?.id,
            reviewsCount = reviewCount,
            offersCount = offerCount
        )
    }

    @Transactional
    override fun getProfileV3(institutionId: Long, googleUid: String?): ProfileResponseV3 {
        val user = googleUid?.let {
            userService.findByGoogleUid(googleUid)
                ?: throw EntityNotFoundException("User with googleUid: $googleUid is not found to database")
        }
        val userZonedDateTime = timestampProvider.getUserZonedDateTime()

        persistentContextProvider.refreshCache()

        val institution = institutionCacheService.findById(institutionId)

        val institutionWrapper = mutableListOf(institution)
            .map { EntityWrapper(it, mutableListOf()) }
            .let { if (user != null) userInterceptorService.handleSavedInstitutions(user, it) else it }
            .let { workTimeInterceptorService.handleWorkTimeInstitutions(SearchInstitutionDays.NOW.name, userZonedDateTime, it) }
            .map { wrapper ->
                wrapper.entity.reviews = institution.reviews.sortedByDescending { it.feedback.length }.toMutableList()
                return@map wrapper
            }.first()

        return institutionWrapper.generateProfileResponseV3(
            dayOfWeek = getDay(SearchInstitutionDays.NOW.name, userZonedDateTime),
            userId = user?.id
        )
    }

}