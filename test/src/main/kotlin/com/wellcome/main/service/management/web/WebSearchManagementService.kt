package com.wellcome.main.service.management.web

import com.wellcome.main.component.TimestampProvider
import com.wellcome.main.dto.web.common.BirthdayCampaignsDto
import com.wellcome.main.dto.web.response.DynamicSearchResponse
import com.wellcome.main.entity.ApplicationConfigType
import com.wellcome.main.entity.institution.Institution
import com.wellcome.main.entity.institution.InstitutionOffer
import com.wellcome.main.entity.story.StoryType
import com.wellcome.main.entity.user.User
import com.wellcome.main.service.extentions.generators.web.common.*
import com.wellcome.main.service.extentions.management.filterByInstitutionCategory
import com.wellcome.main.service.facade.ApplicationConfigService
import com.wellcome.main.service.facade.institution.BirthdayCampaignUserService
import com.wellcome.main.service.facade.institution.InstitutionEventService
import com.wellcome.main.service.facade.institution.InstitutionService
import com.wellcome.main.service.facade.selection.SelectionOfferService
import com.wellcome.main.service.facade.selection.SelectionService
import com.wellcome.main.service.facade.story.StoryService
import com.wellcome.main.service.facade.user.UserService
import com.wellcome.main.service.interceptor.UserInterceptorService
import com.wellcome.main.service.interceptor.WorkTimeInterceptorService
import com.wellcome.main.util.enumerators.SearchInstitutionDays
import com.wellcome.main.util.functions.getDay
import com.wellcome.main.util.functions.getInstitutionCategoryByTime
import com.wellcome.main.wrapper.Delegate
import com.wellcome.main.wrapper.EntityWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.ZonedDateTime
import javax.persistence.EntityNotFoundException

interface WebSearchManagementService {
    fun getDynamicSearch(googleUid: String?, localityId: Long): DynamicSearchResponse
}

@Service
open class DefaultWebSearchManagementService @Autowired constructor(
    private val userService: UserService,
    private val storyService: StoryService,
    private val selectionService: SelectionService,
    private val timestampProvider: TimestampProvider,
    private val institutionService: InstitutionService,
    private val selectionOfferService: SelectionOfferService,
    private val userInterceptorService: UserInterceptorService,
    private val institutionEventService: InstitutionEventService,
    private val applicationConfigService: ApplicationConfigService,
    private val workTimeInterceptorService: WorkTimeInterceptorService,
    private val birthdayCampaignUserService: BirthdayCampaignUserService
) : WebSearchManagementService {

    @Transactional(readOnly = true)
    override fun getDynamicSearch(googleUid: String?, localityId: Long): DynamicSearchResponse {
        val user = googleUid?.let {
            userService.findByGoogleUid(it)
                ?: throw EntityNotFoundException("User width id: $it is not found to database")
        }

        val userZonedDateTime = timestampProvider.getUserZonedDateTime()

        val institutionCount = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.INSTITUTION_RANGING_SEARCH_ATTRIBUTES_SCREEN_COUNT).getLongValueNotNull()
        val eventCount = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.EVENT_RANGING_SEARCH_ATTRIBUTES_SCREEN_COUNT).getLongValueNotNull()
        val storyCount = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.STORY_RANGING_SEARCH_ATTRIBUTES_COUNT).getLongValueNotNull()
        val offerCount = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.OFFER_RANGING_SEARCH_ATTRIBUTES_SCREEN_COUNT).getLongValueNotNull()

        val categoryType = getInstitutionCategoryByTime(userZonedDateTime)

        val birthdayCampaignUser = user?.let(User::getIdNotNull)
            ?.let(birthdayCampaignUserService::findByUser)

        val institutions = institutionService.findByLocality(localityId)

        val offers = institutions
            .filter { it.offers.isNotEmpty() }
            .map(Institution::getWorkingOffers)
            .flatMap(List<InstitutionOffer>::toList)

        val recommendedInstitutionWrappers = institutions
            .filterByInstitutionCategory(categoryType)
            .map { EntityWrapper(it) }
            .let { if (user != null) userInterceptorService.handleSavedInstitutions(user, it) else it }
            .let { workTimeInterceptorService.handleWorkTimeInstitutions(SearchInstitutionDays.NOW.name, userZonedDateTime, it) }
            .filter { wrapper -> (wrapper.delegates.first { it is Delegate.TimeDelegate } as Delegate.TimeDelegate).open }
            .sortedByDescending { it.entity.rating }
            .take(institutionCount.toInt())

        val recommendedOfferWrappers = offers
            .map { EntityWrapper(it) }
            .let { if (user != null) userInterceptorService.handleSavedInstitutionsInsideOffers(user, it) else it }
            .let { workTimeInterceptorService.handleWorkTimeOffers(SearchInstitutionDays.NOW.name, userZonedDateTime, it) }
            .filter { wrapper -> (wrapper.delegates.first { it is Delegate.TimeDelegate } as Delegate.TimeDelegate).open }
            .sortedByDescending { it.entity.getInstitutionRatingNotNull() }

        val eventWrappers = institutionEventService.findNotCompleted()
            .map { EntityWrapper(it, mutableListOf()) }
            .sortedBy { LocalDate.parse(it.entity.date) }
            .take(eventCount.toInt())
            .let { if (user != null) userInterceptorService.handleSavedInstitutionsInsideEvent(user, it) else it }

        val stories = storyService.findAll()
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

        return DynamicSearchResponse(
            recommendedInstitutions = recommendedInstitutionWrappers.generateInstitutionDtoList(dayOfWeek),
            recommendedOffers = recommendedOfferWrappers.generateOfferDtoList(dayOfWeek).take(offerCount.toInt()),
            closestEvents = eventWrappers.generateEventDtoList(dayOfWeek),
            usefulStoryDtoList = usefulStories.generateStoryDtoList(),
            interestingStoryDtoList = interestingStories.generateStoryDtoList(),
            selectionDtoList = selections.generateSelectionDtoList(dayOfWeek, selectionOffers),
            birthdayCampaignsDto = birthdayCampaignUser?.let {
                BirthdayCampaignsDto(
                    id = it.id!!,
                    showBirthdayCampaignsFullScreen = !it.viewed,
                    userDto = user.generateUserDto(),
                    birthdayCampaignsDtoList = it.birthdayCampaigns.toList().generateBirthdayCampaignDtoList()
                )
            }
        )
    }

}