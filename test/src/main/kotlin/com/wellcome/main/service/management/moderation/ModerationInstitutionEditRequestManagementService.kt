package com.wellcome.main.service.management.moderation

import com.wellcome.main.component.TimestampProvider
import com.wellcome.main.dto.admin.common.WorksUpDto
import com.wellcome.main.dto.moderation.request.v1.InstitutionEditRequestRequestV1
import com.wellcome.main.entity.CurrencyType
import com.wellcome.main.entity.Price
import com.wellcome.main.entity.institution.*
import com.wellcome.main.entity.institutionProfile.*
import com.wellcome.main.service.facade.PriceService
import com.wellcome.main.service.facade.institution.*
import com.wellcome.main.service.facade.institutionProfile.*
import com.wellcome.main.util.functions.ifNotEmpty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import javax.persistence.EntityNotFoundException

interface ModerationInstitutionEditRequestManagementService {
    fun edit(request: InstitutionEditRequestRequestV1)
}

@Service
open class DefaultModerationInstitutionEditRequestManagementService @Autowired constructor(
    private val priceService: PriceService,
    private val timestampProvider: TimestampProvider,
    private val institutionService: InstitutionService,
    private val institutionEventService: InstitutionEventService,
    private val institutionOfferService: InstitutionOfferService,
    private val institutionProfileService: InstitutionProfileService,
    private val institutionPictureService: InstitutionPictureService,
    private val institutionEditRequestService: InstitutionEditRequestService,
    private val institutionOfferWorkTimeService: InstitutionOfferWorkTimeService,
    private val institutionEditRequestEventService: InstitutionEditRequestEventService,
    private val institutionEditRequestOfferService: InstitutionEditRequestOfferService,
    private val institutionEditRequestStatusService: InstitutionEditRequestStatusService,
    private val institutionEditRequestPictureService: InstitutionEditRequestPictureService
) : ModerationInstitutionEditRequestManagementService {

    @Transactional
    override fun edit(request: InstitutionEditRequestRequestV1) {
        val currentDate = timestampProvider.getUserZonedDateTime().toLocalDate()

        val institution = institutionService.findById(request.institutionId)

        val institutionProfile = institutionProfileService.findByInstitution(requireNotNull(institution.id))
            ?: throw EntityNotFoundException("Institution profile width institutionId: ${institution.id} is not found to database")

        val institutionEditRequest = institutionEditRequestService
            .findByInstitutionProfile(institutionProfile.id!!)?.apply {
                request.avatarUrl?.let { this.avatarUrl = it }
                request.description?.let { this.description = it }
            } ?: InstitutionEditRequest(
            avatarUrl = request.avatarUrl ?: institution.avatarUrl,
            description = request.description ?: institution.description,
            status = InstitutionEditRequestStatus().let(institutionEditRequestStatusService::saveOrUpdate),
            institutionProfile = institutionProfile).let(institutionEditRequestService::saveOrUpdate)

        institutionEditRequest.status.apply {
            this.status = InstitutionEditRequestFeedBackStatus.IN_REVIEW
        }.let(institutionEditRequestStatusService::saveOrUpdate)

        val deletePictures = mutableListOf<InstitutionEditRequestPicture>()

        request.deletePictureIds.ifNotEmpty()
            ?.asSequence()
            ?.map { Pair(it, institutionEditRequestPictureService.findByPictureId(it)) }
            ?.map { it.also { pair -> pair.second?.let(deletePictures::add) } }
            ?.filter { it.second != null }
            ?.map { it.first }
            ?.map(institutionPictureService::findById)
            ?.map { InstitutionEditRequestPicture(it, InstitutionEditRequestContentStatus.REMOVE, institutionEditRequest) }
            ?.toList()
            ?.let(institutionEditRequestPictureService::saveAll)

        deletePictures.ifNotEmpty()
            ?.mapNotNull(InstitutionEditRequestPicture::id)
            ?.forEach(institutionEditRequestPictureService::deleteById)

        val deleteEvents = mutableListOf<InstitutionEditRequestEvent>()

        request.deleteEventIds.ifNotEmpty()
            ?.asSequence()
            ?.map { Pair(it, institutionEditRequestEventService.findByEvent(it)) }
            ?.map { it.also { pair -> pair.second?.let(deleteEvents::add) } }
            ?.filter { it.second != null }
            ?.map { it.first }
            ?.map(institutionEventService::findById)
            ?.map { InstitutionEditRequestEvent(it, InstitutionEditRequestContentStatus.REMOVE, institutionEditRequest) }
            ?.toList()
            ?.let(institutionEditRequestEventService::saveAll)

        deleteEvents.ifNotEmpty()
            ?.mapNotNull(InstitutionEditRequestEvent::id)
            ?.forEach(institutionEditRequestEventService::deleteById)

        val deleteOffers = mutableListOf<InstitutionEditRequestOffer>()

        request.deleteOfferIds.ifNotEmpty()
            ?.asSequence()
            ?.map { Pair(it, institutionEditRequestOfferService.findByEvent(it)) }
            ?.map { it.also { pair -> pair.second?.let(deleteOffers::add) } }
            ?.filter { it.second != null }
            ?.map { it.first }
            ?.map(institutionOfferService::findById)
            ?.map { InstitutionEditRequestOffer(it, InstitutionEditRequestContentStatus.REMOVE, institutionEditRequest) }
            ?.toList()
            ?.let(institutionEditRequestOfferService::saveAll)

        deleteOffers.ifNotEmpty()
            ?.mapNotNull(InstitutionEditRequestOffer::id)
            ?.forEach(institutionEditRequestOfferService::deleteById)

        request.newPictures.ifNotEmpty()
            ?.map { InstitutionPicture(it, true, institutionProfile.institution) }
            ?.let(institutionPictureService::saveAll)
            ?.map { InstitutionEditRequestPicture(it, InstitutionEditRequestContentStatus.ADD, institutionEditRequest) }
            ?.let(institutionEditRequestPictureService::saveAll)

        val newOffers = mutableListOf<InstitutionOffer>()

        request.newOffers.ifNotEmpty()
            ?.map {
                InstitutionOffer(
                    institution = institutionProfile.institution,
                    title = it.title,
                    text = it.description,
                    pictureUrl = "",
                    startDate = it.startDate,
                    endDate = it.endDate,
                    birthday = it.isBirthday,
                    completed = false,
                    inReview = true,
                    active = LocalDate.parse(it.startDate) <= currentDate,
                    offerType = OfferType.valueOf(it.offerType))
                    .let(institutionOfferService::saveOrUpdate)
                    .also { offer -> newOffers.add(offer) }
                    .let { offer -> Pair(offer, it.worksUp) }
            }
            ?.map {
                it.second
                    .filterNot(WorksUpDto::closed)
                    .map { worksUpDto ->
                        InstitutionOfferWorkTime(
                            institutionOffer = it.first,
                            dayOfWeek = DayOfWeeks.valueOf(worksUpDto.dayOfWeek),
                            startTime = institutionProfile.getStartWork(worksUpDto),
                            endTime = institutionProfile.getEndWork(worksUpDto)
                        )
                    }
            }
            ?.flatMap(List<InstitutionOfferWorkTime>::toList)
            ?.let(institutionOfferWorkTimeService::saveAll)

        newOffers.ifNotEmpty()
            ?.map { InstitutionEditRequestOffer(it, InstitutionEditRequestContentStatus.ADD, institutionEditRequest) }
            ?.let(institutionEditRequestOfferService::saveAll)

        request.newEvents.ifNotEmpty()
            ?.map {
                Price(
                    lowerAmount = BigDecimal(it.price.lowerAmount),
                    topAmount = BigDecimal(it.price.topAmount),
                    fixAmount = BigDecimal(it.price.fixAmount),
                    free = it.price.free,
                    fixPrice = it.price.fixPrice,
                    currencyType = CurrencyType.valueOf(it.price.currencyType))
                    .let(priceService::saveOrUpdate)
                    .let { price -> Pair(it, price) }
            }
            ?.map {
                InstitutionEvent(
                    title = it.first.title,
                    pictureUrl = it.first.pictureUrl,
                    description = it.first.description,
                    startWork = it.first.startWork,
                    date = it.first.date,
                    square = it.first.square,
                    completed = false,
                    institution = institutionProfile.institution,
                    inReview = true,
                    price = it.second)
            }
            ?.let(institutionEventService::saveAll)
            ?.map { InstitutionEditRequestEvent(it, InstitutionEditRequestContentStatus.ADD, institutionEditRequest) }
            ?.let(institutionEditRequestEventService::saveAll)
    }

    private fun InstitutionProfile.getStartWork(worksUpDto: WorksUpDto): String {
        return if (worksUpDto.always) this.institution.workTime.first { workTime ->
            workTime.dayOfWeek == DayOfWeeks.valueOf(worksUpDto.dayOfWeek)
        }.startDay else worksUpDto.startWork
    }

    private fun InstitutionProfile.getEndWork(worksUpDto: WorksUpDto): String {
        return if (worksUpDto.always) this.institution.workTime.first { workTime ->
            workTime.dayOfWeek == DayOfWeeks.valueOf(worksUpDto.dayOfWeek)
        }.endDay else worksUpDto.endWork
    }

}