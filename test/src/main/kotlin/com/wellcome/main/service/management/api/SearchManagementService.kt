package com.wellcome.main.service.management.api

import com.wellcome.main.component.TimestampProvider
import com.wellcome.main.dto.api.newDto.Days
import com.wellcome.main.dto.api.newDto.common.v1.*
import com.wellcome.main.dto.api.newDto.response.v1.DynamicSearchResponseV1
import com.wellcome.main.dto.api.newDto.response.v1.DynamicSearchResultResponseV1
import com.wellcome.main.dto.api.newDto.response.v1.SearchResultResponseV1
import com.wellcome.main.dto.api.newDto.response.v2.DynamicSearchResponseV2
import com.wellcome.main.dto.api.newDto.response.v2.DynamicSearchResultResponseV2
import com.wellcome.main.dto.api.newDto.response.v3.DynamicSearchResponseV3
import com.wellcome.main.dto.api.newDto.response.v3.DynamicSearchResultResponseV3
import com.wellcome.main.entity.ApplicationConfigType
import com.wellcome.main.entity.institution.Institution
import com.wellcome.main.entity.institution.InstitutionCategoryType
import com.wellcome.main.entity.institution.InstitutionEvent
import com.wellcome.main.entity.institution.InstitutionOffer
import com.wellcome.main.entity.story.StoryType
import com.wellcome.main.entity.user.User
import com.wellcome.main.service.cache.InstitutionCacheService
import com.wellcome.main.service.extentions.generators.api.common.*
import com.wellcome.main.service.extentions.generators.api.common.v2.generateOfferDtoV2List
import com.wellcome.main.service.extentions.management.filterByInstitutionCategory
import com.wellcome.main.service.extentions.management.filterNotByInstitutionCategory
import com.wellcome.main.service.extentions.management.sortCategories
import com.wellcome.main.service.facade.ApplicationConfigService
import com.wellcome.main.service.facade.institution.BirthdayCampaignUserService
import com.wellcome.main.service.facade.institution.InstitutionCategoryService
import com.wellcome.main.service.facade.institution.InstitutionEventService
import com.wellcome.main.service.facade.selection.SelectionOfferService
import com.wellcome.main.service.facade.selection.SelectionService
import com.wellcome.main.service.facade.story.StoryService
import com.wellcome.main.service.facade.user.UserService
import com.wellcome.main.service.interceptor.StoreReviewInterceptorService
import com.wellcome.main.service.interceptor.UserInterceptorService
import com.wellcome.main.service.interceptor.WorkTimeInterceptorService
import com.wellcome.main.service.utils.TranslationService
import com.wellcome.main.util.enumerators.SearchInstitutionDays
import com.wellcome.main.util.functions.*
import com.wellcome.main.wrapper.Delegate
import com.wellcome.main.wrapper.EntityWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.ZonedDateTime
import javax.persistence.EntityNotFoundException

interface SearchManagementService {
    fun getDynamicSearchAttributesV1(reviewing: Boolean, localityId: Long, googleUid: String?): DynamicSearchResponseV1
    fun getDynamicSearchAttributesV2(reviewing: Boolean, localityId: Long, googleUid: String?): DynamicSearchResponseV2
    fun getDynamicSearchAttributesV3(reviewing: Boolean, localityId: Long, googleUid: String?): DynamicSearchResponseV3
    fun searchV1(reviewing: Boolean, categoryId: Long, day: String, localityId: Long, googleUid: String?): DynamicSearchResultResponseV1
    fun searchV2(reviewing: Boolean, categoryId: Long, day: String, localityId: Long, googleUid: String?, lat: Double?, lon: Double?): DynamicSearchResultResponseV2
    fun searchV3(reviewing: Boolean, categoryId: Long, day: String, localityId: Long, googleUid: String?): DynamicSearchResultResponseV3
    fun searchBySimilarName(reviewing: Boolean, similarName: String, localityId: Long, googleUid: String?): SearchResultResponseV1
}

@Service
open class DefaultSearchManagementService @Autowired constructor(
    private val userService: UserService,
    private val storyService: StoryService,
    private val selectionService: SelectionService,
    private val timestampProvider: TimestampProvider,
    private val translationService: TranslationService,
    private val selectionOfferService: SelectionOfferService,
    private val userInterceptorService: UserInterceptorService,
    private val institutionCacheService: InstitutionCacheService,
    private val institutionEventService: InstitutionEventService,
    private val applicationConfigService: ApplicationConfigService,
    private val workTimeInterceptorService: WorkTimeInterceptorService,
    private val institutionCategoryService: InstitutionCategoryService,
    private val birthdayCampaignUserService: BirthdayCampaignUserService,
    private val storeReviewInterceptorService: StoreReviewInterceptorService
) : SearchManagementService {

    @Transactional(readOnly = true)
    override fun getDynamicSearchAttributesV1(reviewing: Boolean, localityId: Long, googleUid: String?): DynamicSearchResponseV1 {
        val user = googleUid?.let {
            userService.findByGoogleUid(googleUid)
                ?: throw EntityNotFoundException("User with googleUid: $googleUid is not found to database")
        }
        val userZonedDateTime = timestampProvider.getUserZonedDateTime()

        val categories = institutionCategoryService.findRanging()
            .sortCategories()

        val categoryType = getInstitutionCategoryByTime(userZonedDateTime)

        val institutions = institutionCacheService.findByLocality(localityId)

        val institutionCount = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.INSTITUTION_RANGING_SEARCH_ATTRIBUTES_SCREEN_COUNT).getLongValueNotNull()
        val offerCount = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.OFFER_RANGING_SEARCH_ATTRIBUTES_SCREEN_COUNT).getLongValueNotNull()

        val recommendedInstitutionWrappers = institutions
            .filterByInstitutionCategory(categoryType)
            .shuffled()
            .map { EntityWrapper(it) }
            .let { if (user != null) userInterceptorService.handleSavedInstitutions(user, it) else it }
            .let { workTimeInterceptorService.handleWorkTimeInstitutions(SearchInstitutionDays.NOW.name, userZonedDateTime, it) }
            .let { if (reviewing) storeReviewInterceptorService.handleForbiddenInstitutions(it) else it }
            .filter { wrapper -> (wrapper.delegates.first { it is Delegate.TimeDelegate } as Delegate.TimeDelegate).open }
            .let { wrappers ->
                return@let if (reviewing) wrappers.filter { wrapper ->
                    (wrapper.delegates.first { it is Delegate.StoreReviewDelegate } as Delegate.StoreReviewDelegate).allowed
                } else wrappers
            }
            .sortedByDescending { it.entity.rating }
            .take(institutionCount.toInt())

        val recommendedOfferWrappers = institutions
            .filter { it.offers.isNotEmpty() }
            .map(Institution::getWorkingOffers)
            .flatMap(List<InstitutionOffer>::toList)
            .map { EntityWrapper(it) }
            .let { if (user != null) userInterceptorService.handleSavedInstitutionsInsideOffers(user, it) else it }
            .let { workTimeInterceptorService.handleWorkTimeOffers(SearchInstitutionDays.NOW.name, userZonedDateTime, it) }
            .let { if (reviewing) storeReviewInterceptorService.handleForbiddenOffers(it) else it }
            .filter { wrapper -> (wrapper.delegates.first { it is Delegate.TimeDelegate } as Delegate.TimeDelegate).open }
            .let { wrappers ->
                return@let if (reviewing) wrappers.filter { wrapper ->
                    (wrapper.delegates.first { it is Delegate.StoreReviewDelegate } as Delegate.StoreReviewDelegate).allowed
                } else wrappers
            }
            .sortedByDescending { it.entity.getInstitutionRatingNotNull() }

        val eventWrappers = institutionEventService.findNotCompleted()
            .map { EntityWrapper(it, mutableListOf()) }
            .let { if (user != null) userInterceptorService.handleSavedInstitutionsInsideEvent(user, it) else it }
            .let { workTimeInterceptorService.handleWorkTimeEvents(SearchInstitutionDays.TODAY.name, userZonedDateTime, it) }
            .let { if (reviewing) storeReviewInterceptorService.handleForbiddenEvents(it) else it }
            .filter { wrapper -> (wrapper.delegates.first { it is Delegate.TimeDelegate } as Delegate.TimeDelegate).open }
            .let { wrappers ->
                return@let if (reviewing) wrappers.filter { wrapper ->
                    (wrapper.delegates.first { it is Delegate.StoreReviewDelegate } as Delegate.StoreReviewDelegate).allowed
                } else wrappers
            }

        val dayOfWeek = getDay(SearchInstitutionDays.NOW.name, userZonedDateTime)

        val categoryOptions =
            if (reviewing) categories.filterNot {
                it.categoryType == InstitutionCategoryType.HOOKAH
                    || it.categoryType == InstitutionCategoryType.STRIP_BAR
                    || it.categoryType == InstitutionCategoryType.VAPE_BAR
            }
            else categories

        return DynamicSearchResponseV1(
            categoryOption = CategoryOptionDtoV1(
                title = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.FIRST_QUESTION_TITLE).getStringValueNotNull(),
                categories = categoryOptions.generateCategoryDtoV1List()
            ),
            timeOptionDto = TimeOptionDtoV1(
                title = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.SECOND_QUESTION_TITLE).getStringValueNotNull(),
                times = generateTimesDtoV1List(userZonedDateTime)
            ),
            recommendedInstitutions = BlockInstitutionsDtoV1(
                title = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.RECOMMENDED_PLACES_TITLE).getStringValueNotNull(),
                institutions = recommendedInstitutionWrappers.generateInstitutionDtoV1List(dayOfWeek)
            ),
            recommendedOffers = BlockOffersDtoV1(
                title = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.RECOMMENDED_OFFERS_TITLE).getStringValueNotNull(),
                offers = recommendedOfferWrappers.generateOfferDtoV1List(dayOfWeek).take(offerCount.toInt()),
                blockType = BlockType.DYNAMIC_SEARCH_OFFERS.name,
                showAll = recommendedOfferWrappers.size > offerCount
            ),
            todayEvents = BlockEventDtoV1(
                title = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.TODAY_EVENTS_TITLE).getStringValueNotNull(),
                events = eventWrappers.generateEventDtoV1List(dayOfWeek)
            )
        )
    }

    @Transactional(readOnly = true)
    override fun getDynamicSearchAttributesV2(reviewing: Boolean, localityId: Long, googleUid: String?): DynamicSearchResponseV2 {
        val user = googleUid?.let {
            userService.findByGoogleUid(googleUid)
                ?: throw EntityNotFoundException("User with googleUid: $googleUid is not found to database")
        }
        val userZonedDateTime = timestampProvider.getUserZonedDateTime()

        val categoryType = getInstitutionCategoryByTime(userZonedDateTime)

        val institutions = institutionCacheService.findByLocality(localityId)

        val institutionCount = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.INSTITUTION_RANGING_SEARCH_ATTRIBUTES_SCREEN_COUNT).getLongValueNotNull()
        val eventCount = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.EVENT_RANGING_SEARCH_ATTRIBUTES_SCREEN_COUNT).getLongValueNotNull()
        val storyCount = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.STORY_RANGING_SEARCH_ATTRIBUTES_COUNT).getLongValueNotNull()
        val offerCount = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.OFFER_RANGING_SEARCH_ATTRIBUTES_SCREEN_COUNT).getLongValueNotNull()

        val recommendedInstitutionWrappers = institutions
            .filterByInstitutionCategory(categoryType)
            .map { EntityWrapper(it) }
            .let { if (user != null) userInterceptorService.handleSavedInstitutions(user, it) else it }
            .let { workTimeInterceptorService.handleWorkTimeInstitutions(SearchInstitutionDays.NOW.name, userZonedDateTime, it) }
            .let { if (reviewing) storeReviewInterceptorService.handleForbiddenInstitutions(it) else it }
            .filter { wrapper -> (wrapper.delegates.first { it is Delegate.TimeDelegate } as Delegate.TimeDelegate).open }
            .let { wrappers ->
                return@let if (reviewing) wrappers.filter { wrapper ->
                    (wrapper.delegates.first { it is Delegate.StoreReviewDelegate } as Delegate.StoreReviewDelegate).allowed
                } else wrappers
            }
            .sortedByDescending { it.entity.rating }
            .take(institutionCount.toInt())

        val recommendedOfferWrappers = institutions
            .filter { it.offers.isNotEmpty() }
            .map(Institution::getWorkingOffers)
            .flatMap(List<InstitutionOffer>::toList)
            .map { EntityWrapper(it) }
            .let { if (user != null) userInterceptorService.handleSavedInstitutionsInsideOffers(user, it) else it }
            .let { workTimeInterceptorService.handleWorkTimeOffers(SearchInstitutionDays.NOW.name, userZonedDateTime, it) }
            .let { if (reviewing) storeReviewInterceptorService.handleForbiddenOffers(it) else it }
            .filter { wrapper -> (wrapper.delegates.first { it is Delegate.TimeDelegate } as Delegate.TimeDelegate).open }
            .let { wrappers ->
                return@let if (reviewing) wrappers.filter { wrapper ->
                    (wrapper.delegates.first { it is Delegate.StoreReviewDelegate } as Delegate.StoreReviewDelegate).allowed
                } else wrappers
            }
            .sortedByDescending { it.entity.getInstitutionRatingNotNull() }

        val eventWrappers = institutionEventService.findNotCompleted()
            .map { EntityWrapper(it, mutableListOf()) }
            .sortedBy { LocalDate.parse(it.entity.date) }
            .take(eventCount.toInt())
            .let { if (user != null) userInterceptorService.handleSavedInstitutionsInsideEvent(user, it) else it }
            .let { if (reviewing) storeReviewInterceptorService.handleForbiddenEvents(it) else it }
            .let { wrappers ->
                return@let if (reviewing) wrappers.filter { wrapper ->
                    (wrapper.delegates.first { it is Delegate.StoreReviewDelegate } as Delegate.StoreReviewDelegate).allowed
                } else wrappers
            }

        val stories = storyService.findAll()
            .filterNot { it.reviewForbiddenContent && reviewing }
            .sortedByDescending {
                (it.updateEntityDateTime ?: it.createEntityDateTime)
                    .let(::requireNotNull)
                    .let(ZonedDateTime::parse)
            }

        val usefulStories = stories
            .filter { it.type == StoryType.USEFUL }
            .take(storyCount.toInt())
            .map { EntityWrapper(it) }
            .let { if (user != null) userInterceptorService.handleLikedStory(user, it) else it }

        val interestingStories = stories
            .filter { it.type == StoryType.INTERESTING }
            .take(storyCount.toInt())
            .map { EntityWrapper(it) }
            .let { if (user != null) userInterceptorService.handleLikedStory(user, it) else it }

        val selectionOffers = selectionOfferService.findAll()
            .filterNot { it.offer.completed }
            .map { EntityWrapper(it) }
            .let { if (user != null) userInterceptorService.handleSavedInstitutionInsideOfferInsideSelection(user, it) else it }

        val selections = selectionService.findAll()

        val dayOfWeek = getDay(SearchInstitutionDays.NOW.name, userZonedDateTime)

        return DynamicSearchResponseV2(
            recommendedInstitutions = recommendedInstitutionWrappers.generateInstitutionDtoV1List(dayOfWeek),
            recommendedOffers = recommendedOfferWrappers.generateOfferDtoV2List(dayOfWeek).take(offerCount.toInt()),
            closestEvents = eventWrappers.generateEventDtoV1List(dayOfWeek),
            usefulStoryDtoList = usefulStories.generateStoryDtoV1List(),
            interestingStoryDtoList = interestingStories.generateStoryDtoV1List(),
            selectionDtoList = selections.generateSelectionDtoV1List(dayOfWeek, selectionOffers)
        )
    }

    @Transactional(readOnly = true)
    override fun getDynamicSearchAttributesV3(reviewing: Boolean, localityId: Long, googleUid: String?): DynamicSearchResponseV3 {
        val user = googleUid?.let {
            userService.findByGoogleUid(googleUid)
                ?: throw EntityNotFoundException("User with googleUid: $googleUid is not found to database")
        }
        val institutions = institutionCacheService.findByLocality(localityId)

        val userZonedDateTime = timestampProvider.getUserZonedDateTime()

        val categoryType = getInstitutionCategoryByTime(userZonedDateTime)

        val institutionCount = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.INSTITUTION_RANGING_SEARCH_ATTRIBUTES_SCREEN_COUNT).getLongValueNotNull()
        val eventCount = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.EVENT_RANGING_SEARCH_ATTRIBUTES_SCREEN_COUNT).getLongValueNotNull()
        val storyCount = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.STORY_RANGING_SEARCH_ATTRIBUTES_COUNT).getLongValueNotNull()
        val offerCount = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.OFFER_RANGING_SEARCH_ATTRIBUTES_SCREEN_COUNT).getLongValueNotNull()

        val birthdayCampaignUser = user?.let(User::getIdNotNull)
            ?.let(birthdayCampaignUserService::findByUser)

        val recommendedInstitutionWrappers = institutions
            .filterByInstitutionCategory(categoryType)
            .map { EntityWrapper(it) }
            .let { if (user != null) userInterceptorService.handleSavedInstitutions(user, it) else it }
            .let { workTimeInterceptorService.handleWorkTimeInstitutions(SearchInstitutionDays.NOW.name, userZonedDateTime, it) }
            .let { if (reviewing) storeReviewInterceptorService.handleForbiddenInstitutions(it) else it }
            .filter { wrapper -> (wrapper.delegates.first { it is Delegate.TimeDelegate } as Delegate.TimeDelegate).open }
            .let { wrappers ->
                return@let if (reviewing) wrappers.filter { wrapper ->
                    (wrapper.delegates.first { it is Delegate.StoreReviewDelegate } as Delegate.StoreReviewDelegate).allowed
                } else wrappers
            }
            .sortedByDescending { it.entity.rating }
            .take(institutionCount.toInt())

        val recommendedOfferWrappers = institutions
            .filter { it.offers.isNotEmpty() }
            .map(Institution::getWorkingOffers)
            .flatMap(List<InstitutionOffer>::toList)
            .map { EntityWrapper(it) }
            .let { if (user != null) userInterceptorService.handleSavedInstitutionsInsideOffers(user, it) else it }
            .let { workTimeInterceptorService.handleWorkTimeOffers(SearchInstitutionDays.NOW.name, userZonedDateTime, it) }
            .let { if (reviewing) storeReviewInterceptorService.handleForbiddenOffers(it) else it }
            .filter { wrapper -> (wrapper.delegates.first { it is Delegate.TimeDelegate } as Delegate.TimeDelegate).open }
            .let { wrappers ->
                return@let if (reviewing) wrappers.filter { wrapper ->
                    (wrapper.delegates.first { it is Delegate.StoreReviewDelegate } as Delegate.StoreReviewDelegate).allowed
                } else wrappers
            }
            .sortedByDescending { it.entity.getInstitutionRatingNotNull() }

        val eventWrappers = institutionEventService.findNotCompleted()
            .map { EntityWrapper(it, mutableListOf()) }
            .sortedBy { LocalDate.parse(it.entity.date) }
            .take(eventCount.toInt())
            .let { if (user != null) userInterceptorService.handleSavedInstitutionsInsideEvent(user, it) else it }
            .let { if (reviewing) storeReviewInterceptorService.handleForbiddenEvents(it) else it }
            .let { wrappers ->
                return@let if (reviewing) wrappers.filter { wrapper ->
                    (wrapper.delegates.first { it is Delegate.StoreReviewDelegate } as Delegate.StoreReviewDelegate).allowed
                } else wrappers
            }

        val stories = storyService.findAll()
            .filterNot { it.reviewForbiddenContent && reviewing }
            .sortedByDescending {
                (it.updateEntityDateTime ?: it.createEntityDateTime)
                    .let(::requireNotNull)
                    .let(ZonedDateTime::parse)
            }

        val usefulStories = stories
            .filter { it.type == StoryType.USEFUL }
            .take(storyCount.toInt())
            .map { EntityWrapper(it) }
            .let { if (user != null) userInterceptorService.handleLikedStory(user, it) else it }

        val interestingStories = stories
            .filter { it.type == StoryType.INTERESTING }
            .take(storyCount.toInt())
            .map { EntityWrapper(it) }
            .let { if (user != null) userInterceptorService.handleLikedStory(user, it) else it }

        val selectionOffers = selectionOfferService.findAll()
            .filterNot { it.offer.completed }
            .map { EntityWrapper(it) }
            .let { if (user != null) userInterceptorService.handleSavedInstitutionInsideOfferInsideSelection(user, it) else it }

        val selections = selectionService.findAll()

        val dayOfWeek = getDay(SearchInstitutionDays.NOW.name, userZonedDateTime)

        return DynamicSearchResponseV3(
            recommendedInstitutions = recommendedInstitutionWrappers.generateInstitutionDtoV1List(dayOfWeek),
            recommendedOffers = recommendedOfferWrappers.generateOfferDtoV2List(dayOfWeek).take(offerCount.toInt()),
            closestEvents = eventWrappers.generateEventDtoV1List(dayOfWeek),
            usefulStoryDtoList = usefulStories.generateStoryDtoV1List(),
            interestingStoryDtoList = interestingStories.generateStoryDtoV1List(),
            selectionDtoList = selections.generateSelectionDtoV1List(dayOfWeek, selectionOffers),
            birthdayCampaignsDto = birthdayCampaignUser?.let {
                BirthdayCampaignsDtoV1(
                    id = it.id!!,
                    showBirthdayCampaignsFullScreen = !it.viewed,
                    userDto = user.generateUserDtoV1(),
                    birthdayCampaignsDtoList = it.birthdayCampaigns.toList().generateBirthdayCampaignDtoV1List()
                )
            }
        )
    }

    @Transactional(readOnly = true)
    override fun searchV1(reviewing: Boolean, categoryId: Long, day: String, localityId: Long, googleUid: String?): DynamicSearchResultResponseV1 {
        val user = googleUid?.let {
            userService.findByGoogleUid(googleUid)
                ?: throw EntityNotFoundException("User with googleUid: $googleUid is not found to database")
        }

        val userZonedDateTime = timestampProvider.getUserZonedDateTime()

        val category = institutionCategoryService.findById(categoryId)

        val institutions = institutionCacheService.findByLocality(localityId)

        val categoryInstitutions = institutions
            .filterByInstitutionCategory(category.categoryType)

        val otherInstitutions = institutions
            .filterNotByInstitutionCategory(category.categoryType)

        val institutionCount = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.INSTITUTION_RANGING_SEARCH_SCREEN_COUNT).getLongValueNotNull()
        val offerCount = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.OFFER_RANGING_SEARCH_SCREEN_COUNT).getLongValueNotNull()

        val categoryInstitutionWrappers = categoryInstitutions
            .map { EntityWrapper(it) }
            .let { if (user != null) userInterceptorService.handleSavedInstitutions(user, it) else it }
            .let { workTimeInterceptorService.handleWorkTimeInstitutions(SearchInstitutionDays.NOW.name, userZonedDateTime, it) }
            .let { if (reviewing) storeReviewInterceptorService.handleForbiddenInstitutions(it) else it }
            .filter { wrapper -> (wrapper.delegates.first { it is Delegate.TimeDelegate } as Delegate.TimeDelegate).open }
            .let { wrappers ->
                return@let if (reviewing) wrappers.filter { wrapper ->
                    (wrapper.delegates.first { it is Delegate.StoreReviewDelegate } as Delegate.StoreReviewDelegate).allowed
                } else wrappers
            }
            .sortedByDescending { it.entity.rating }

        val recommendedInstitutionWrappers = categoryInstitutionWrappers
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
            }
            .shuffled()

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
            }
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
                .let { if (reviewing) storeReviewInterceptorService.handleForbiddenEvents(it) else it }
                .filter { wrapper -> (wrapper.delegates.first { it is Delegate.TimeDelegate } as Delegate.TimeDelegate).open }
                .let { wrappers ->
                    return@let if (reviewing) wrappers.filter { wrapper ->
                        (wrapper.delegates.first { it is Delegate.StoreReviewDelegate } as Delegate.StoreReviewDelegate).allowed
                    } else wrappers
                }

        val dayOfWeek = getDay(day, userZonedDateTime)

        return DynamicSearchResultResponseV1(
            title = category.title,
            recommendedInstitutions = BlockInstitutionsDtoV1(
                title = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.RECOMMENDED_PLACES_RESULT_TITLE).getStringValueNotNull().plusCategoryCompletion(category.categoryType),
                institutions = recommendedInstitutionWrappers.generateInstitutionDtoV1List(dayOfWeek)
            ),
            offers = BlockOffersDtoV1(
                title = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.RECOMMENDED_OFFERS_RESULT_TITLE).getStringValueNotNull().plusCategoryCompletionV2(category.categoryType),
                offers = recommendedOfferWrappers.generateOfferDtoV1List(dayOfWeek).take(offerCount.toInt()),
                showAll = recommendedOfferWrappers.size > offerCount,
                blockType = BlockType.FEED_OFFERS.name
            ),
            allInstitutions = BlockInstitutionsDtoV1(
                title = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.OTHER_PLACES_TITLE).getStringValueNotNull().plusCategoryCompletion(category.categoryType),
                institutions = categoryInstitutionWrappers.generateInstitutionDtoV1List(dayOfWeek)
            ),
            otherOffers = BlockOffersDtoV1(
                title = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.OTHER_OFFERS_TITLE).getStringValueNotNull(),
                offers = otherOfferWrappers.generateOfferDtoV1List(dayOfWeek).take(offerCount.toInt()),
                showAll = otherOfferWrappers.size > offerCount,
                blockType = BlockType.FEED_OFFERS_INTERESTING.name
            ),
            categoryInstitutionEvents = BlockEventDtoV1(
                title = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.CATEGORY_INSTITUTION_EVENTS).getStringValueNotNull().plusCategoryCompletionV2(category.categoryType).plus(Days.valueOf(day).getEnding()),
                events = categoryInstitutionEventWrappers.generateEventDtoV1List(dayOfWeek)
            )
        )
    }

    @Transactional(readOnly = true)
    override fun searchV2(reviewing: Boolean, categoryId: Long, day: String, localityId: Long, googleUid: String?, lat: Double?, lon: Double?): DynamicSearchResultResponseV2 {
        val user = googleUid?.let {
            userService.findByGoogleUid(googleUid)
                ?: throw EntityNotFoundException("User with googleUid: $googleUid is not found to database")
        }

        val userZonedDateTime = timestampProvider.getUserZonedDateTime()

        val category = institutionCategoryService.findById(categoryId)

        val institutions = institutionCacheService.findByLocality(localityId)

        val categoryInstitutions = institutions
            .filterByInstitutionCategory(category.categoryType)

        val otherInstitutions = institutions
            .filterNotByInstitutionCategory(category.categoryType)

        val institutionCount = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.INSTITUTION_RANGING_SEARCH_SCREEN_COUNT).getLongValueNotNull()
        val offerCount = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.OFFER_RANGING_SEARCH_SCREEN_COUNT).getLongValueNotNull()

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
            }
            .sortedByDescending { it.entity.rating }

        val recommendedInstitutionWrappers = categoryInstitutionWrappers
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
            }
            .shuffled()

        val closestInstitutionWrappers =
            if (lat == null || lon == null) emptyList()
            else categoryInstitutionWrappers
                .map { wrapper ->
                    distanceBetweenLatLon(lat, lon,
                        wrapper.entity.locationAttributes.lat,
                        wrapper.entity.locationAttributes.lon)
                        .calculateDistance()
                        .toInt().let { Pair(requireNotNull(wrapper.entity.id), it) }
                }
                .sortedBy(Pair<Long, Int>::second)
                .map { pair ->
                    categoryInstitutionWrappers.first {
                        it.entity.id == pair.first
                    }
                }.take(institutionCount.toInt())

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
            }
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
                .let { if (reviewing) storeReviewInterceptorService.handleForbiddenEvents(it) else it }
                .filter { wrapper -> (wrapper.delegates.first { it is Delegate.TimeDelegate } as Delegate.TimeDelegate).open }
                .let { wrappers ->
                    return@let if (reviewing) wrappers.filter { wrapper ->
                        (wrapper.delegates.first { it is Delegate.StoreReviewDelegate } as Delegate.StoreReviewDelegate).allowed
                    } else wrappers
                }

        val dayOfWeek = getDay(day, userZonedDateTime)

        return DynamicSearchResultResponseV2(
            title = category.title,
            recommendedInstitutions = BlockInstitutionsDtoV1(
                title = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.RECOMMENDED_PLACES_RESULT_TITLE).getStringValueNotNull().plusCategoryCompletion(category.categoryType),
                institutions = recommendedInstitutionWrappers.generateInstitutionDtoV1List(dayOfWeek)
            ),
            offers = BlockOffersDtoV1(
                title = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.RECOMMENDED_OFFERS_RESULT_TITLE).getStringValueNotNull().plusCategoryCompletionV2(category.categoryType),
                offers = recommendedOfferWrappers.generateOfferDtoV1List(dayOfWeek).take(offerCount.toInt()),
                showAll = recommendedOfferWrappers.size > offerCount,
                blockType = BlockType.FEED_OFFERS.name
            ),
            closestInstitutions = BlockInstitutionsDtoV1(
                title = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.CLOSEST_INSTITUTION_TITLE).getStringValueNotNull().plusCategoryCompletion(category.categoryType),
                institutions = closestInstitutionWrappers.generateInstitutionDtoV1List(dayOfWeek)
            ),
            allInstitutions = BlockInstitutionsDtoV1(
                title = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.OTHER_PLACES_TITLE).getStringValueNotNull().plusCategoryCompletion(category.categoryType),
                institutions = categoryInstitutionWrappers.generateInstitutionDtoV1List(dayOfWeek)
            ),
            otherOffers = BlockOffersDtoV1(
                title = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.OTHER_OFFERS_TITLE).getStringValueNotNull(),
                offers = otherOfferWrappers.generateOfferDtoV1List(dayOfWeek).take(offerCount.toInt()),
                showAll = otherOfferWrappers.size > offerCount,
                blockType = BlockType.FEED_OFFERS_INTERESTING.name
            ),
            categoryInstitutionEvents = BlockEventDtoV1(
                title = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.CATEGORY_INSTITUTION_EVENTS).getStringValueNotNull().plusCategoryCompletionV2(category.categoryType).plus(Days.valueOf(day).getEnding()),
                events = categoryInstitutionEventWrappers.generateEventDtoV1List(dayOfWeek)
            )
        )
    }

    @Transactional(readOnly = true)
    override fun searchV3(reviewing: Boolean, categoryId: Long, day: String, localityId: Long, googleUid: String?): DynamicSearchResultResponseV3 {
        val user = googleUid?.let {
            userService.findByGoogleUid(googleUid)
                ?: throw EntityNotFoundException("User with googleUid: $googleUid is not found to database")
        }
        val userZonedDateTime = timestampProvider.getUserZonedDateTime()

        val category = institutionCategoryService.findById(categoryId)

        val institutions = institutionCacheService.findByLocality(localityId)

        val categoryInstitutions = institutions
            .filterByInstitutionCategory(category.categoryType)

        val otherInstitutions = institutions
            .filterNotByInstitutionCategory(category.categoryType)

        val institutionCount = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.INSTITUTION_RANGING_SEARCH_SCREEN_COUNT).getLongValueNotNull()
        val offerCount = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.OFFER_RANGING_SEARCH_SCREEN_COUNT).getLongValueNotNull()

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

        val recommendedInstitutionWrappers = categoryInstitutionWrappers
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

        return DynamicSearchResultResponseV3(
            recommendedInstitutions = recommendedInstitutionWrappers.generateInstitutionDtoV1List(dayOfWeek),
            offers = recommendedOfferWrappers.generateOfferDtoV2List(dayOfWeek).take(offerCount.toInt()),
            allInstitutions = categoryInstitutionWrappers.generateInstitutionDtoV1List(dayOfWeek),
            otherOffers = otherOfferWrappers.generateOfferDtoV2List(dayOfWeek).take(offerCount.toInt()),
            categoryInstitutionEvents = categoryInstitutionEventWrappers.generateEventDtoV1List(dayOfWeek)
        )
    }

    @Transactional(readOnly = true)
    override fun searchBySimilarName(reviewing: Boolean, similarName: String, localityId: Long, googleUid: String?): SearchResultResponseV1 {
        val user = googleUid?.let {
            userService.findByGoogleUid(googleUid)
                ?: throw EntityNotFoundException("User with googleUid: $googleUid is not found to database")
        }
        if (similarName.isEmpty()) return SearchResultResponseV1(emptyList())

        val userZonedDateTime = timestampProvider.getUserZonedDateTime()

        val institutions = institutionCacheService.findByLocality(localityId)

        val similarInstitutions = mutableListOf<Institution>()

        if (similarName.length == 1) institutions.filter {
            it.name[0].toLowerCase() == similarName[0].toLowerCase() || it.name[0].toLowerCase() == translationService.translateAny(similarName)[0].toLowerCase()
        }.let(similarInstitutions::addAll)

        if (similarName.length > 1) institutions.filter {
            it.name.contains(similarName, true) || it.name.contains(translationService.translateAny(similarName), true)
        }.let(similarInstitutions::addAll)

        val similarInstitutionWrappers = similarInstitutions
            .map { EntityWrapper(it, mutableListOf()) }
            .let { if (user != null) userInterceptorService.handleSavedInstitutions(user, it) else it }
            .let { if (reviewing) storeReviewInterceptorService.handleForbiddenInstitutions(it) else it }
            .let { workTimeInterceptorService.handleWorkTimeInstitutions(SearchInstitutionDays.NOW.name, userZonedDateTime, it) }
            .let { wrappers ->
                return@let if (reviewing) wrappers.filter { wrapper ->
                    (wrapper.delegates.first { it is Delegate.StoreReviewDelegate } as Delegate.StoreReviewDelegate).allowed
                } else wrappers
            }

        return similarInstitutionWrappers.generateInstitutionDtoV1List(getDay(SearchInstitutionDays.NOW.name, userZonedDateTime)).let(::SearchResultResponseV1)
    }


}