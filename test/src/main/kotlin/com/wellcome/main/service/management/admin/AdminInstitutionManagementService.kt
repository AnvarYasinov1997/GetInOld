package com.wellcome.main.service.management.admin

import com.wellcome.main.annotations.ReloadCache
import com.wellcome.main.component.TimestampProvider
import com.wellcome.main.dto.admin.common.EventDto
import com.wellcome.main.dto.admin.common.OfferDto
import com.wellcome.main.dto.admin.common.TagDto
import com.wellcome.main.dto.admin.common.WorksUpDto
import com.wellcome.main.dto.admin.request.AddInstitutionRequest
import com.wellcome.main.dto.admin.request.EditInstitutionRequest
import com.wellcome.main.dto.admin.response.InstitutionModerationResponse
import com.wellcome.main.dto.admin.response.InstitutionNameResponse
import com.wellcome.main.dto.admin.response.ModerationPriority
import com.wellcome.main.dto.admin.response.PhoneDto
import com.wellcome.main.entity.CurrencyType
import com.wellcome.main.entity.Price
import com.wellcome.main.entity.institution.*
import com.wellcome.main.entity.institutionProfile.*
import com.wellcome.main.service.extentions.createInstitutionFromRow
import com.wellcome.main.service.extentions.generators.admin.generateInstitutionModerationResponse
import com.wellcome.main.service.extentions.generators.admin.generateInstitutionNameDto
import com.wellcome.main.service.extentions.management.sortByName
import com.wellcome.main.service.extentions.toPhones
import com.wellcome.main.service.facade.LocalityService
import com.wellcome.main.service.facade.PriceService
import com.wellcome.main.service.facade.institution.*
import com.wellcome.main.service.facade.institutionProfile.InstitutionEditRequestService
import com.wellcome.main.service.facade.institutionProfile.InstitutionEditRequestStatusService
import com.wellcome.main.service.facade.selection.SelectionService
import com.wellcome.main.service.facade.user.UserService
import com.wellcome.main.service.utils.LogMessage
import com.wellcome.main.service.utils.LoggerService
import com.wellcome.main.util.functions.ifNotEmpty
import com.wellcome.main.util.functions.toInstagramLink
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal
import java.time.ZonedDateTime
import javax.persistence.EntityNotFoundException

interface AdminInstitutionManagementService {
    fun fill(userId: Long)
    fun block(institutionId: Long, userId: Long)
    fun getById(id: Long): InstitutionModerationResponse
    fun add(request: AddInstitutionRequest, localityId: Long, userId: Long)
    fun edit(request: EditInstitutionRequest, userId: Long)
    fun createFromFile(dataFile: MultipartFile, localityId: Long)
    fun getOneByLocality(localityId: Long): InstitutionModerationResponse
    fun getNames(categoryId: Long, localityId: Long): InstitutionNameResponse
    fun getAllNames(localityId: Long): InstitutionNameResponse
}

@Service
open class DefaultAdminInstitutionManagementService @Autowired constructor(
    private val userService: UserService,
    private val priceService: PriceService,
    private val loggerService: LoggerService,
    private val localityService: LocalityService,
    private val selectionService: SelectionService,
    private val timestampProvider: TimestampProvider,
    private val institutionService: InstitutionService,
    private val institutionTagService: InstitutionTagService,
    private val mapsInstitutionService: MapsInstitutionService,
    private val institutionOfferService: InstitutionOfferService,
    private val institutionEventService: InstitutionEventService,
    private val institutionProfileService: InstitutionProfileService,
    private val institutionPictureService: InstitutionPictureService,
    private val institutionWorkTimeService: InstitutionWorkTimeService,
    private val institutionCategoryService: InstitutionCategoryService,
    private val institutionEditRequestService: InstitutionEditRequestService,
    private val institutionContactPhoneService: InstitutionContactPhoneService,
    private val institutionOfferWorkTimeService: InstitutionOfferWorkTimeService,
    private val institutionEditRequestStatusService: InstitutionEditRequestStatusService
) : AdminInstitutionManagementService {

    @Transactional(readOnly = true)
    override fun getById(id: Long): InstitutionModerationResponse {
        val institution = institutionService.findById(id)

        val institutionProfile =
            institutionProfileService.findByInstitution(requireNotNull(institution.id))

        val selections = selectionService.findAll()

        val categories = institutionCategoryService.findAll()

        val institutionEditRequest = institutionProfile?.let {
            institutionEditRequestService.findByInstitutionProfile(requireNotNull(it.id))
        }

        return institution.generateInstitutionModerationResponse(
            0, institutionEditRequest, categories, selections)
    }

    @Synchronized
    @Transactional
    override fun getOneByLocality(localityId: Long): InstitutionModerationResponse {
        val institutions = institutionService.findIntactByLocality(localityId)

        val institution = institutions.firstOrNull { !it.approved }
            ?: institutions.firstOrNull()
            ?: throw EntityNotFoundException("All institutions with locality id: $localityId already moderated")

        institution.apply {
            this.processing = true
        }.let(institutionService::saveOrUpdate)

        val categories = institutionCategoryService.findAll()

        val selections = selectionService.findAll()

        return institution.generateInstitutionModerationResponse(
            institutions.size, null, categories, selections)
    }

    @Transactional(readOnly = true)
    override fun getNames(categoryId: Long, localityId: Long): InstitutionNameResponse {
        return localityId.let(institutionService::findByLocalityForAdmin)
            .filter { institution ->
                institution.categories.firstOrNull { it.id == categoryId } != null
            }
            .let(List<Institution>::sortByName)
            .map { it.generateInstitutionNameDto() }
            .let(::InstitutionNameResponse)
    }

    @Transactional(readOnly = true)
    override fun getAllNames(localityId: Long): InstitutionNameResponse {
        val institutions = institutionService.findIntactByLocality(localityId)
            .filter { it.createEntityDateTime != null }

        val userZonedDateTime = timestampProvider.getUserZonedDateTime()

        val lowerPriorityInstitutions = mutableListOf<Pair<ModerationPriority, Institution>>()

        val middlePriorityInstitutions = mutableListOf<Pair<ModerationPriority, Institution>>()

        val highPriorityInstitutions = mutableListOf<Pair<ModerationPriority, Institution>>()

        for (i in institutions) {
            val lastModerationDateTime = i.updateEntityDateTime?.let(ZonedDateTime::parse)
                ?: i.createEntityDateTime?.let(ZonedDateTime::parse)
                ?: continue
            if (lastModerationDateTime.plusWeeks(2) <= userZonedDateTime) {
                highPriorityInstitutions.add(Pair(ModerationPriority.HIGH, i))
                continue
            }
            if (lastModerationDateTime.plusWeeks(1) <= userZonedDateTime
                && i.getWorkingOffers().isNotEmpty()) {
                highPriorityInstitutions.add(Pair(ModerationPriority.HIGH, i))
                continue
            }
            if (lastModerationDateTime.plusWeeks(1) <= userZonedDateTime) {
                middlePriorityInstitutions.add(Pair(ModerationPriority.MIDDLE, i))
                continue
            }
            lowerPriorityInstitutions.add(Pair(ModerationPriority.LOW, i))
        }

        return InstitutionNameResponse(
            (highPriorityInstitutions + middlePriorityInstitutions + lowerPriorityInstitutions)
                .map { it.second.generateInstitutionNameDto(it.first) }
        )
    }

    @Transactional
    override fun block(institutionId: Long, userId: Long) {
        institutionService.findById(institutionId).apply {
            this.blocked = true
            this.approved = false
            this.processing = false
            this.moderator = userService.findById(userId)
        }.let(institutionService::saveOrUpdate)
    }

    @ReloadCache
    @Transactional
    override fun add(request: AddInstitutionRequest, localityId: Long, userId: Long) {
        val locality = localityService.findById(localityId)
        val categories = request.newCategories.map(institutionCategoryService::findById).toMutableSet()
        val institution = Institution(
            name = request.name,
            description = request.description,
            comments = request.comments,
            avatarUrl = request.avatarUrl,
            moderator = userService.findById(userId),
            locality = locality,
            locationAttributes = InstitutionLocationAttributes(
                lat = request.lat,
                lon = request.lon,
                address = request.address
            ),
            instagramAccount = request.instagram,
            categories = categories,
            rating = 0.0,
            peopleOfRatedCount = 0,
            ranging = categories.checkRanging(),
            blocked = request.blocked,
            processing = false,
            approved = true
        ).let(institutionService::saveOrUpdate)
        institution.addInstitutionRelationship(
            pictureUrls = request.newPictureUrls,
            worksUps = request.worksUp,
            contactPhones = request.newPhones.map(PhoneDto::phoneNumber),
            offers = request.newOffers,
            events = request.newEvents,
            tags = request.newTags)
    }

    @ReloadCache
    @Transactional
    override fun edit(request: EditInstitutionRequest, userId: Long) {
        val institution = institutionService.findById(request.institutionId)
        institution.apply {
            this.processing = false
            this.name = request.name
            this.blocked = request.blocked
            this.approved = !request.blocked
            this.avatarUrl = request.avatarUrl
            this.description = request.description
            this.comments = request.comments
            this.instagramAccount = request.instagram.toInstagramLink()
            this.locationAttributes.address = request.address
            this.moderator = userService.findById(userId)
        }.apply {
            request.removeCategories.forEach { categoryId ->
                this.categories.remove(institutionCategoryService.findById(categoryId))
            }
            request.newCategories.forEach { categoryId ->
                this.categories.add(institutionCategoryService.findById(categoryId))
            }
        }.apply {
            this.ranging = this.categories.checkRanging()
        }.also {
            it.addInstitutionRelationship(
                pictureUrls = request.newPictureUrls,
                worksUps = request.worksUp,
                contactPhones = request.newPhones.map(PhoneDto::phoneNumber),
                offers = request.newOffers,
                events = request.newEvents,
                tags = request.newTags)
        }
        request.removeTags.forEach(institutionTagService::deleteById)
        request.removePictureIds.forEach(institutionPictureService::deleteById)
        request.removedPhoneIds.forEach(institutionContactPhoneService::deleteById)
        request.removedOfferIds.ifNotEmpty()
            ?.map(institutionOfferService::findById)
            ?.map {
                it.apply {
                    this.completed = true
                    this.active = false
                    this.endDate = timestampProvider
                        .getUserZonedDateTimeByTimeZoneId(requireNotNull(this.institution).locality.timezone)
                        .toLocalDate()
                        .toString()
                }
            }?.let(institutionOfferService::saveAll)
        request.removedEventIds.ifNotEmpty()
            ?.map(institutionEventService::findById)
            ?.map {
                it.apply { this.completed = true }
            }?.let(institutionEventService::saveAll)

        val editRequestDto = request.editRequestFeedbackDto

        if (editRequestDto != null) {
            val institutionEditRequest =
                institutionEditRequestService.findById(editRequestDto.institutionEditRequestId)

            when (editRequestDto.approve) {
                true -> {
                    institution.apply {
                        this.avatarUrl = institutionEditRequest.avatarUrl
                        this.description = institutionEditRequest.description
                    }
                    institutionEditRequest.offers.ifNotEmpty()
                        ?.filter { it.status == InstitutionEditRequestContentStatus.ADD }
                        ?.map(InstitutionEditRequestOffer::offer)
                        ?.map {
                            it.apply {
                                this.inReview = false
                                this.startDate = timestampProvider
                                    .getUserZonedDateTimeByTimeZoneId(requireNotNull(this.institution).locality.timezone)
                                    .toLocalDate()
                                    .toString()
                            }
                        }?.let(institutionOfferService::saveAll)
                    institutionEditRequest.offers.ifNotEmpty()
                        ?.filter { it.status == InstitutionEditRequestContentStatus.REMOVE }
                        ?.map(InstitutionEditRequestOffer::offer)
                        ?.map {
                            it.apply {
                                this.active = false
                                this.endDate = timestampProvider
                                    .getUserZonedDateTimeByTimeZoneId(requireNotNull(this.institution).locality.timezone)
                                    .toLocalDate()
                                    .toString()
                            }
                        }?.let(institutionOfferService::saveAll)
                    institutionEditRequest.events.ifNotEmpty()
                        ?.filter { it.status == InstitutionEditRequestContentStatus.ADD }
                        ?.map(InstitutionEditRequestEvent::event)
                        ?.map {
                            it.apply { this.inReview = false }
                        }?.let(institutionEventService::saveAll)
                    institutionEditRequest.events.ifNotEmpty()
                        ?.filter { it.status == InstitutionEditRequestContentStatus.REMOVE }
                        ?.map(InstitutionEditRequestEvent::event)
                        ?.map {
                            it.apply { this.completed = true }
                        }?.let(institutionEventService::saveAll)
                    institutionEditRequest.pictures.ifNotEmpty()
                        ?.filter { it.status == InstitutionEditRequestContentStatus.ADD }
                        ?.map(InstitutionEditRequestPicture::picture)
                        ?.map {
                            it.apply { this.inReview = false }
                        }?.let(institutionPictureService::saveAll)
                    institutionEditRequest.pictures.ifNotEmpty()
                        ?.filter { it.status == InstitutionEditRequestContentStatus.REMOVE }
                        ?.map(InstitutionEditRequestPicture::picture)
                        ?.mapNotNull(InstitutionPicture::id)
                        ?.forEach(institutionPictureService::deleteById)
                    institutionEditRequest.apply {
                        this.approved = true
                    }.let(institutionEditRequestService::saveOrUpdate)
                    institutionEditRequest.status.apply {
                        this.status = InstitutionEditRequestFeedBackStatus.APPROVED
                    }.let(institutionEditRequestStatusService::saveOrUpdate)
                }
                false -> {
                    institutionEditRequest.status.apply {
                        this.status = InstitutionEditRequestFeedBackStatus.KICKBACK
                        this.developerMessage = editRequestDto.message
                    }.let(institutionEditRequestStatusService::saveOrUpdate)
                }
            }
        }
        institutionService.saveOrUpdate(institution)
    }

    @Transactional
    override fun createFromFile(dataFile: MultipartFile, localityId: Long) {
        val locality = localityService.findById(localityId)
        val workBook = XSSFWorkbook(dataFile.inputStream)
        val sheet = workBook.getSheetAt(0)
        for (it in 1 until sheet.physicalNumberOfRows) {
            sheet.getRow(it).createInstitutionFromRow(locality).let(mapsInstitutionService::saveOrUpdate)
        }
    }

    @Transactional
    override fun fill(userId: Long) {
        val nonCreatedInstitutions = mapsInstitutionService.findAllNonCreated()
        nonCreatedInstitutions
            .filter { it.instagram != null }
            .forEach { mapsInstitution ->
                try {
                    val phones = mapsInstitution.toPhones()

                    Institution(
                        name = mapsInstitution.name,
                        description = "",
                        comments = "",
                        avatarUrl = "",
                        moderator = userService.findById(userId),
                        locality = mapsInstitution.locality,
                        locationAttributes = InstitutionLocationAttributes(
                            lat = mapsInstitution.lat,
                            lon = mapsInstitution.lon,
                            address = mapsInstitution.address ?: ""
                        ),
                        instagramAccount = mapsInstitution.instagram,
                        categories = mutableSetOf(),
                        rating = 0.0,
                        peopleOfRatedCount = 0,
                        processing = false,
                        approved = false,
                        blocked = false,
                        ranging = false
                    ).let(institutionService::saveOrUpdate)
                        .addInstitutionRelationship(emptyList(), emptyList(), phones, emptyList(), emptyList(), emptyList())

                    mapsInstitution.apply { this.created = true }
                        .let(mapsInstitutionService::saveOrUpdate)

                } catch (e: Exception) {
                    loggerService.warning(LogMessage("FILL INSTITUTION: $e"), e)
                }
            }
    }

    private fun Set<InstitutionCategory>.checkRanging(): Boolean {
        for (it in this)
            if (it.categoryType == InstitutionCategoryType.COFFEE_HOUSE ||
                it.categoryType == InstitutionCategoryType.RESTAURANT) return false
        return true
    }

    private fun Institution.addInstitutionRelationship(pictureUrls: List<String>,
                                                       worksUps: List<WorksUpDto>,
                                                       contactPhones: List<String>,
                                                       tags: List<TagDto>,
                                                       offers: List<OfferDto>,
                                                       events: List<EventDto>) {
        contactPhones.ifNotEmpty()
            ?.map { InstitutionContactPhone(it, this) }
            ?.let(institutionContactPhoneService::saveAll)

        tags.ifNotEmpty()
            ?.map { InstitutionTag(it.tagName, this) }
            ?.let(institutionTagService::saveAll)

        pictureUrls.ifNotEmpty()
            ?.map { InstitutionPicture(it, false, this) }
            ?.let(institutionPictureService::saveAll)

        worksUps.ifNotEmpty()?.let { x ->
            if (x.size == 7) {
                this.workTime.mapNotNull(InstitutionWorkTime::id)
                    .forEach(institutionWorkTimeService::deleteById)
                x.map {
                    InstitutionWorkTime(
                        startDay = if (!it.closed) it.startWork else "",
                        endDay = if (!it.closed) it.endWork else "",
                        closed = it.closed,
                        dayOfWeek = DayOfWeeks.valueOf(it.dayOfWeek),
                        institution = this
                    )
                }.let(institutionWorkTimeService::saveAll)
            }
        }
        events.ifNotEmpty()?.map { eventDto ->
            val price = eventDto.price.let {
                Price(
                    lowerAmount = BigDecimal(it.lowerAmount),
                    topAmount = BigDecimal(it.topAmount),
                    fixAmount = BigDecimal(it.fixAmount),
                    free = it.free,
                    fixPrice = it.fixPrice,
                    currencyType = CurrencyType.valueOf(it.currencyType)
                )
            }.let(priceService::saveOrUpdate)
            InstitutionEvent(
                title = eventDto.title,
                pictureUrl = eventDto.pictureUrl,
                description = eventDto.description,
                startWork = eventDto.startWork,
                date = eventDto.date,
                square = eventDto.square,
                completed = false,
                institution = this,
                inReview = false,
                price = price
            )
        }?.let(institutionEventService::saveAll)
        offers.ifNotEmpty()?.forEach { offerDto ->
            val offerStartDate = timestampProvider
                .getUserZonedDateTimeByTimeZoneId(this.locality.timezone)
                .toLocalDate()
                .toString()
            val offer = InstitutionOffer(
                institution = this,
                title = offerDto.title,
                text = offerDto.description,
                pictureUrl = "",
                startDate = offerStartDate,
                endDate = offerDto.endDate,
                birthday = offerDto.isBirthday,
                completed = false,
                inReview = false,
                active = true,
                offerType = OfferType.valueOf(offerDto.offerType)
            ).let(institutionOfferService::saveOrUpdate)
            offerDto.worksUp
                .filterNot(WorksUpDto::closed)
                .forEach { workUp ->
                    if (!workUp.always) {
                        InstitutionOfferWorkTime(
                            institutionOffer = offer,
                            dayOfWeek = DayOfWeeks.valueOf(workUp.dayOfWeek),
                            startTime = workUp.startWork,
                            endTime = workUp.endWork
                        ).let(institutionOfferWorkTimeService::saveOrUpdate)
                    } else {
                        val institutionWorkTime = this.workTime
                            .firstOrNull { it.dayOfWeek == DayOfWeeks.valueOf(workUp.dayOfWeek) }
                        if (!requireNotNull(institutionWorkTime).closed) {
                            InstitutionOfferWorkTime(
                                institutionOffer = offer,
                                dayOfWeek = DayOfWeeks.valueOf(workUp.dayOfWeek),
                                startTime = institutionWorkTime.startDay,
                                endTime = institutionWorkTime.endDay
                            ).let(institutionOfferWorkTimeService::saveOrUpdate)
                        }

                    }
                }
        }
    }

}