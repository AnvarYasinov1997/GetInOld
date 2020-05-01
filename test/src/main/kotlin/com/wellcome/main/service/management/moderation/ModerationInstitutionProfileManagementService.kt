package com.wellcome.main.service.management.moderation

import com.wellcome.main.component.TimestampProvider
import com.wellcome.main.configuration.security.model.ProfileModel
import com.wellcome.main.dto.api.newDto.common.v1.PictureDtoV1
import com.wellcome.main.dto.moderation.common.v1.*
import com.wellcome.main.dto.moderation.response.v1.*
import com.wellcome.main.entity.institution.DayOfWeeks
import com.wellcome.main.entity.institution.InstitutionEvent
import com.wellcome.main.entity.institution.InstitutionOffer
import com.wellcome.main.entity.institution.InstitutionPicture
import com.wellcome.main.entity.institutionProfile.InstitutionEditRequestContentStatus
import com.wellcome.main.entity.institutionProfile.InstitutionEditRequestEvent
import com.wellcome.main.entity.institutionProfile.InstitutionEditRequestOffer
import com.wellcome.main.entity.institutionProfile.InstitutionEditRequestPicture
import com.wellcome.main.model.CommonAnalyticModel
import com.wellcome.main.service.analyticFacade.AnalyticService
import com.wellcome.main.service.extentions.generators.api.common.*
import com.wellcome.main.service.extentions.generators.api.common.v2.generateOfferDtoV2
import com.wellcome.main.service.extentions.generators.moderation.common.generateInstructionDtoV1List
import com.wellcome.main.service.extentions.generators.moderation.common.generateMarketingDtoV1List
import com.wellcome.main.service.facade.BookmarkService
import com.wellcome.main.service.facade.InstructionService
import com.wellcome.main.service.facade.MarketingService
import com.wellcome.main.service.facade.institution.InstitutionProfileService
import com.wellcome.main.service.facade.institution.InstitutionService
import com.wellcome.main.service.facade.institutionProfile.InstitutionEditRequestService
import com.wellcome.main.util.variables.AnalyticEventValues
import com.wellcome.main.util.variables.MobileAnalyticEvent
import com.wellcome.main.wrapper.EntityWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime
import java.time.Month
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.stream.Stream
import javax.persistence.EntityNotFoundException

interface ModerationInstitutionProfileManagementService {
    fun getInstitutionProfiles(models: List<ProfileModel>): ModerationInstitutionResponseV1
    fun getArchive(institutionId: Long): ArchiveResponseV1
    fun getOfferAnalytic(institutionId: Long): OfferAnalyticResponseV1
    fun getEventAnalytic(institutionId: Long): EventAnalyticResponseV1
    fun getDashboard(institutionId: Long, latestFetch: String?): DashboardResponseV1
    fun getModerationInstitutionProfileForEdit(institutionId: Long): InstitutionModerationResponseForEditV1
    fun getOldOfferAnalytic(institutionId: Long): OldOfferAnalyticResponseV1
    fun getOldEventAnalytic(institutionId: Long): OldEventAnalyticResponseV1
    fun getMarketingContent(marketingId: Long): MarketingResponseV1
    fun getInstructionContent(instructionId: Long): InstructionResponseV1
}

@Service
open class DefaultModerationInstitutionProfileManagementService @Autowired constructor(
    private val analyticService: AnalyticService,
    private val bookmarkService: BookmarkService,
    private val marketingService: MarketingService,
    private val timestampProvider: TimestampProvider,
    private val institutionService: InstitutionService,
    private val instructionService: InstructionService,
    private val institutionProfileService: InstitutionProfileService,
    private val institutionEditRequestService: InstitutionEditRequestService,
    @Qualifier("managementService") private val threadPoolExecutor: Executor
) : ModerationInstitutionProfileManagementService {

    @Transactional(readOnly = true)
    override fun getInstitutionProfiles(models: List<ProfileModel>): ModerationInstitutionResponseV1 {
        val institutions = models.map {
            institutionProfileService.findByLogin(it.login).institution
        }
        return ModerationInstitutionResponseV1(institutions.map { it.generateInstitutionDtoV1() })
    }

    @Transactional(readOnly = true)
    override fun getMarketingContent(marketingId: Long): MarketingResponseV1 {
        val marketing = marketingService.findById(marketingId)
        return MarketingResponseV1(
            id = marketing.id!!,
            text = marketing.text,
            title = marketing.title,
            pictureUrl = marketing.pictureUrl)
    }

    @Transactional(readOnly = true)
    override fun getInstructionContent(instructionId: Long): InstructionResponseV1 {
        val instruction = instructionService.findById(instructionId)
        return InstructionResponseV1(
            id = instruction.id!!,
            title = instruction.title,
            text = instruction.text,
            backgroundColor = instruction.backgroundColor.hexCode,
            type = instruction.type.name,
            actionType = instruction.actionType.name
        )
    }

    @Transactional(readOnly = true)
    override fun getArchive(institutionId: Long): ArchiveResponseV1 {
        val institution = institutionService.findById(institutionId)
        val offers = institution.offers
            .filter(InstitutionOffer::completed)
            .map { EntityWrapper(it) }

        val events = institution.events
            .filter(InstitutionEvent::completed)
            .map { EntityWrapper(it) }

        return ArchiveResponseV1(
            offers = offers.generateOfferDtoV1List(DayOfWeeks.MONDAY),
            events = events.generateEventDtoV1List(DayOfWeeks.MONDAY)
        )
    }

    @Transactional(readOnly = true)
    override fun getOfferAnalytic(institutionId: Long): OfferAnalyticResponseV1 {
        val offers = institutionService.findById(institutionId)
            .offers.filterNot(InstitutionOffer::completed)
            .sortedByDescending(InstitutionOffer::getIdNotNull)

        return OfferAnalyticResponseV1(offers.map {
            OfferAnalyticDtoV1(
                offerDto = it.generateOfferDtoV2(),
                createDate = it.createEntityDateTime,
                expireDate = it.updateEntityDateTime
            )
        })
    }

    @Transactional(readOnly = true)
    override fun getOldOfferAnalytic(institutionId: Long): OldOfferAnalyticResponseV1 {
        val offers = institutionService.findById(institutionId)
            .offers.filter(InstitutionOffer::completed)
            .sortedByDescending(InstitutionOffer::getIdNotNull)

        return OldOfferAnalyticResponseV1(offers.map {
            OfferAnalyticDtoV1(
                offerDto = it.generateOfferDtoV2(),
                createDate = it.createEntityDateTime,
                expireDate = it.updateEntityDateTime
            )
        })
    }

    @Transactional(readOnly = true)
    override fun getEventAnalytic(institutionId: Long): EventAnalyticResponseV1 {
        val events = institutionService.findById(institutionId)
            .events.filterNot(InstitutionEvent::completed)
            .sortedByDescending(InstitutionEvent::getIdNotNull)

        return EventAnalyticResponseV1(events.map {
            EventAnalyticDtoV1(
                eventDto = it.generateEventDtoV1(),
                createDate = it.createEntityDateTime,
                expireDate = it.date
            )
        })
    }

    @Transactional(readOnly = true)
    override fun getOldEventAnalytic(institutionId: Long): OldEventAnalyticResponseV1 {
        val events = institutionService.findById(institutionId)
            .events.filter(InstitutionEvent::completed)
            .sortedByDescending(InstitutionEvent::getIdNotNull)

        return OldEventAnalyticResponseV1(events.map {
            EventAnalyticDtoV1(
                eventDto = it.generateEventDtoV1(),
                createDate = it.createEntityDateTime,
                expireDate = it.date
            )
        })
    }

    @Transactional(timeout = 30)
    override fun getDashboard(institutionId: Long, latestFetch: String?): DashboardResponseV1 {
        val institution = institutionService.findById(institutionId)

        val userDate = timestampProvider.getUserZonedDateTime().toLocalDate()

        val timezone = institution.locality.timezone

        val coldRange = this.getColdRangeDate(latestFetch, userDate)
        val hotRange = this.getHotRangeDate(userDate)

        val coldAnalyticDataFuture = CompletableFuture<Pair<String, List<CommonAnalyticModel>>>()
        val hotAnalyticDataFuture = CompletableFuture<Pair<String, List<CommonAnalyticModel>>>()

        val coldDateAnalyticData = mutableMapOf<LocalDate, MutableList<CommonAnalyticModel>>()
        val hotDateAnalyticData = mutableMapOf<LocalDate, MutableList<CommonAnalyticModel>>()

        threadPoolExecutor.execute {
            analyticService
                .findCommonAnalytics(institution.id!!, coldRange.first, coldRange.second)
                .let { Pair("cold", it) }
                .let(coldAnalyticDataFuture::complete)
        }

        threadPoolExecutor.execute {
            analyticService
                .findCommonAnalytics(institution.id!!, hotRange.first, hotRange.second)
                .let { Pair("hot", it) }
                .let(hotAnalyticDataFuture::complete)
        }

        val numberOfShared = analyticService.findSharedCount(institution.id!!, "20191201", coldRange.second)

        val numberOfReviewed = institution.reviews.size

        val numberOfSaved = bookmarkService.findByInstitution(institution.id!!).size

        val marketings = marketingService.findAll()

        val instructions = instructionService.findAll()

        Stream.of(hotAnalyticDataFuture, coldAnalyticDataFuture)
            .parallel()
            .map(CompletableFuture<Pair<String, List<CommonAnalyticModel>>>::join)
            .map(::requireNotNull)
            .forEach { pair ->
                pair.second.sortedBy { model ->
                    timestampProvider
                        .epochNanosecondsToZonedDateTime(model.timestamp, timezone)
                }.forEach { model ->
                    val date = timestampProvider
                        .epochNanosecondsToZonedDateTime(model.timestamp, timezone)
                        .toLocalDate()
                    when (pair.first) {
                        "cold" -> {
                            if (coldDateAnalyticData[date] == null) {
                                coldDateAnalyticData[date] = mutableListOf(model)
                            } else coldDateAnalyticData[date]!!.add(model)
                        }
                        "hot" -> {
                            if (hotDateAnalyticData[date] == null) {
                                hotDateAnalyticData[date] = mutableListOf(model)
                            } else hotDateAnalyticData[date]!!.add(model)
                        }
                    }
                }
            }

        return DashboardResponseV1(
            saved = numberOfSaved.toLong(),
            reviews = numberOfReviewed.toLong(),
            shared = numberOfShared,
            latestFetch = userDate.toString(),
            instructionDtoList = instructions.generateInstructionDtoV1List(),
            marketingDtoList = marketings.generateMarketingDtoV1List(),
            coldAnalyticDto = this.generateAnalyticData(timezone, coldDateAnalyticData),
            hotAnalyticDto = this.generateAnalyticData(timezone, hotDateAnalyticData)
        )
    }

    @Transactional(readOnly = true)
    override fun getModerationInstitutionProfileForEdit(institutionId: Long): InstitutionModerationResponseForEditV1 {
        val institution = institutionService.findById(institutionId)

        val institutionProfile = institutionProfileService.findByInstitution(requireNotNull(institution.id))
            ?: throw throw EntityNotFoundException("Institution profile width institutionId: ${institution.id} is not found to database")

        val institutionEditRequest = institutionEditRequestService.findByInstitutionProfile(requireNotNull(institutionProfile.id))

        val institutionEditRequestDeleteOfferIds =
            institutionEditRequest?.offers
                ?.filter { it.status == InstitutionEditRequestContentStatus.REMOVE }
                ?.map(InstitutionEditRequestOffer::offer)
                ?.mapNotNull(InstitutionOffer::id)
                ?: emptyList()
        val offers = mutableListOf<EntityWrapper<InstitutionOffer>>()
        institution.getWorkingOffers()
            .filterNot { institutionEditRequestDeleteOfferIds.contains(requireNotNull(it.id)) }
            .map { EntityWrapper(it) }
            .let(offers::addAll)
        institutionEditRequest?.offers
            ?.filter { it.status == InstitutionEditRequestContentStatus.ADD }
            ?.map(InstitutionEditRequestOffer::offer)
            ?.map { EntityWrapper(it) }
            ?.let(offers::addAll)

        val institutionEditRequestDeleteEventIds =
            institutionEditRequest?.events
                ?.filter { it.status == InstitutionEditRequestContentStatus.REMOVE }
                ?.map(InstitutionEditRequestEvent::event)
                ?.mapNotNull(InstitutionEvent::id)
                ?: emptyList()
        val events = mutableListOf<EntityWrapper<InstitutionEvent>>()
        institution.getWorkingEvents()
            .filterNot { institutionEditRequestDeleteEventIds.contains(requireNotNull(it.id)) }
            .map { EntityWrapper(it) }
            .let(events::addAll)
        institutionEditRequest?.events
            ?.filter { it.status == InstitutionEditRequestContentStatus.ADD }
            ?.map(InstitutionEditRequestEvent::event)
            ?.map { EntityWrapper(it) }
            ?.let(events::addAll)

        val institutionEditRequestDeletePictureIds =
            institutionEditRequest?.pictures
                ?.filter { it.status == InstitutionEditRequestContentStatus.REMOVE }
                ?.map(InstitutionEditRequestPicture::picture)
                ?.mapNotNull(InstitutionPicture::id)
                ?: emptyList()

        val pictures = mutableListOf<InstitutionPicture>()
        institution.pictures
            .filterNot(InstitutionPicture::inReview)
            .filterNot { institutionEditRequestDeletePictureIds.contains(requireNotNull(it.id)) }
            .let(pictures::addAll)
        institutionEditRequest?.pictures
            ?.filter { it.status == InstitutionEditRequestContentStatus.ADD }
            ?.map(InstitutionEditRequestPicture::picture)
            ?.let(pictures::addAll)

        return InstitutionModerationResponseForEditV1(
            institutionDto = institution.generateInstitutionDtoV1(),
            offers = offers.generateOfferDtoV1List(DayOfWeeks.MONDAY),
            events = events.generateEventDtoV1List(DayOfWeeks.MONDAY),
            pictures = pictures.map { PictureDtoV1(requireNotNull(it.id), it.pictureUrl) }
        )
    }

    private fun generateAnalyticData(timezone: String,
                                     dateAnalyticData: Map<LocalDate, MutableList<CommonAnalyticModel>>): AnalyticDtoV1 {
        val institutionSeenModels = mutableMapOf<LocalDate, List<Pair<LocalTime, CommonAnalyticModel>>>()

        val offerSeenModels = mutableMapOf<LocalDate, List<Pair<LocalTime, CommonAnalyticModel>>>()

        val eventSeenModels = mutableMapOf<LocalDate, List<Pair<LocalTime, CommonAnalyticModel>>>()

        val expandEventModels = mutableMapOf<LocalDate, List<Pair<LocalTime, CommonAnalyticModel>>>()

        val expandOfferModels = mutableMapOf<LocalDate, List<Pair<LocalTime, CommonAnalyticModel>>>()

        val institutionProfileClickModels = mutableMapOf<LocalDate, List<Pair<LocalTime, CommonAnalyticModel>>>()

        val showMapModels = mutableMapOf<LocalDate, List<Pair<LocalTime, CommonAnalyticModel>>>()

        val callModels = mutableMapOf<LocalDate, List<Pair<LocalTime, CommonAnalyticModel>>>()

        val taxiModels = mutableMapOf<LocalDate, List<Pair<LocalTime, CommonAnalyticModel>>>()

        val buildRouteModels = mutableMapOf<LocalDate, List<Pair<LocalTime, CommonAnalyticModel>>>()

        val birthdayCampaignSeenModels = mutableMapOf<LocalDate, List<Pair<LocalTime, CommonAnalyticModel>>>()

        val birthdayCampaignUseModels = mutableMapOf<LocalDate, List<Pair<LocalTime, CommonAnalyticModel>>>()

        MobileAnalyticEvent.values().map(MobileAnalyticEvent::value).forEach { eventValue ->
            dateAnalyticData.forEach { map ->
                map.value
                    .filter { it.name == eventValue }
                    .map {
                        val time = timestampProvider
                            .epochNanosecondsToZonedDateTime(it.timestamp, timezone)
                            .toLocalTime()
                        time to it
                    }
                    .sortedBy(Pair<LocalTime, CommonAnalyticModel>::first)
                    .let {
                        if (it.isNotEmpty()) {
                            when (eventValue) {
                                AnalyticEventValues.InstitutionSeen -> institutionSeenModels[map.key] = it
                                AnalyticEventValues.OfferSeen -> offerSeenModels[map.key] = it
                                AnalyticEventValues.EventSeen -> eventSeenModels[map.key] = it
                                AnalyticEventValues.OfferExpand -> expandOfferModels[map.key] = it
                                AnalyticEventValues.EventExpand -> expandEventModels[map.key] = it
                                AnalyticEventValues.InstitutionProfileClick -> institutionProfileClickModels[map.key] = it
                                AnalyticEventValues.ShowMap -> showMapModels[map.key] = it
                                AnalyticEventValues.MakeACall -> callModels[map.key] = it
                                AnalyticEventValues.CallATaxi -> taxiModels[map.key] = it
                                AnalyticEventValues.BuildRoute -> buildRouteModels[map.key] = it
                                AnalyticEventValues.BirthdayCampaignSeen -> birthdayCampaignSeenModels[map.key] = it
                                AnalyticEventValues.BirthdayCampaignUse -> birthdayCampaignUseModels[map.key] = it
                            }
                        }
                    }
            }
        }

        return AnalyticDtoV1(
            userSeenDtoV1 = UserSeenDtoV1(
                dailyInstitutionSeenDtoV1List = institutionSeenModels.generateDailyInstitutionSeenDtoV1List(),
                dailyOfferSeenDtoV1List = offerSeenModels.generateDailyOfferDtoV1List(),
                dailyEventSeenDtoV1List = eventSeenModels.generateDailyEventSeenDtoV1List(),
                dailyBirthdayCampaignDtoList = birthdayCampaignSeenModels.generateDailyBirthdayCampaignSeenDtoV1List()
            ),
            interestingDtoV1 = InterestingDtoV1(
                dailyInstitutionProfileClickDtoV1List = institutionProfileClickModels.generateDailyInstitutionProfileClickDtoV1List(),
                dailyExpandOfferDtoV1List = expandOfferModels.generateDailyExpandOfferDtoV1List(),
                dailyExpandEventDtoV1List = expandEventModels.generateDailyExpandEventDtoV1List()
            ),
            conversionDtoV1 = ConversionDtoV1(
                dailyShowMapDtoV1List = showMapModels.generateDailyShowMapDtoV1List(),
                dailyCallDtoV1List = callModels.generateDailyCallDtoV1List(),
                dailyTaxiDtoV1List = taxiModels.generateDailyTaxiDtoV1List(),
                dailyBuildRouteDtoV1List = buildRouteModels.generateDailyBuildRouteDtoV1List(),
                dailyBirthdayCampaignUseDtoV1List = birthdayCampaignUseModels.generateDailyBirthdayCampaignUseDtoV1List()
            )
        )
    }

    private fun getHotRangeDate(userDate: LocalDate): Pair<String, String> {
        val topDate = userDate

        val lowDate =
            userDate.minusDays(4)

        val lowRangeDay = lowDate.dayOfMonth.toString()
            .let { if (it.length == 1) "0$it" else it }
        val lowRangeMonth = lowDate.month.value.toString()
            .let { if (it.length == 1) "0$it" else it }

        val lowRangeYear = lowDate.year.toString()

        val topRangeDay = topDate.dayOfMonth.toString()
            .let { if (it.length == 1) "0$it" else it }

        val topRangeMonth = topDate.month.value.toString()
            .let { if (it.length == 1) "0$it" else it }

        val topRangeYear = topDate.year.toString()

        val lowRange = timestampProvider.concatenateDateTimeByArguments(lowRangeYear, lowRangeMonth, lowRangeDay)
        val topRange = timestampProvider.concatenateDateTimeByArguments(topRangeYear, topRangeMonth, topRangeDay)

        return Pair(lowRange, topRange)
    }

    private fun getColdRangeDate(latestFetch: String?, userDate: LocalDate): Pair<String, String> {
        val topDate =
            userDate.minusDays(5)

        val lowDate = latestFetch?.let(LocalDate::parse)

        val lowRangeDay = lowDate?.dayOfMonth?.toString() ?: "01"
            .let { if (it.length == 1) "0$it" else it }

        val lowRangeMonth =
            lowDate?.month?.value?.toString()
                ?: Month.DECEMBER.value.toString()
                    .let { if (it.length == 1) "0$it" else it }

        val lowRangeYear = lowDate?.year?.toString() ?: "2019"

        val topRangeDay = topDate.dayOfMonth.toString()
            .let { if (it.length == 1) "0$it" else it }

        val topRangeMonth = topDate.month.value.toString()
            .let { if (it.length == 1) "0$it" else it }

        val topRangeYear = topDate.year.toString()

        val lowRange = timestampProvider.concatenateDateTimeByArguments(lowRangeYear, lowRangeMonth, lowRangeDay)
        val topRange = timestampProvider.concatenateDateTimeByArguments(topRangeYear, topRangeMonth, topRangeDay)

        return Pair(lowRange, topRange)
    }
}