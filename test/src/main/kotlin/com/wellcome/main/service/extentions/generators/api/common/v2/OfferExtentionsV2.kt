package com.wellcome.main.service.extentions.generators.api.common.v2

import com.wellcome.main.dto.api.newDto.common.v1.WorksUpDtoV1
import com.wellcome.main.dto.api.newDto.common.v2.BlockOffersDtoV2
import com.wellcome.main.dto.api.newDto.common.v2.OfferDtoV2
import com.wellcome.main.entity.institution.DayOfWeeks
import com.wellcome.main.entity.institution.InstitutionOffer
import com.wellcome.main.entity.institution.OfferType
import com.wellcome.main.service.extentions.generators.api.common.generateInstitutionDtoV1
import com.wellcome.main.wrapper.Delegate
import com.wellcome.main.wrapper.EntityWrapper
import java.time.ZonedDateTime

fun List<EntityWrapper<InstitutionOffer>>.generateOfferDtoV2List(dayOfWeek: DayOfWeeks): List<OfferDtoV2> =
    this.map {
        var userDelegate: Delegate.UserDelegate? = null

        it.delegates.forEach { delegate ->
            when (delegate) {
                is Delegate.UserDelegate -> userDelegate = delegate
            }
        }
        OfferDtoV2(
            id = requireNotNull(it.entity.id),
            title = it.entity.title,
            description = it.entity.text,
            type = it.entity.offerType.name,
            photoUrl = it.entity.pictureUrl,
            birthday = it.entity.birthday,
            latestCheck = (it.entity.updateEntityDateTime ?: it.entity.createEntityDateTime)
                ?.let(ZonedDateTime::parse)?.toLocalDate()?.toString() ?: "",
            worksUpDtoList = it.entity.workTime.map { x ->
                WorksUpDtoV1(
                    dayOfWeek = x.dayOfWeek.name,
                    startWork = x.startTime,
                    endWork = x.endTime,
                    closed = false,
                    always = x.startTime == "00:00" && x.endTime == "00:00"
                )
            },
            institutionDto = it.entity
                .getInstitutionNotNull()
                .generateInstitutionDtoV1(dayOfWeek, null,
                    userDelegate?.saved ?: false, userDelegate?.rated ?: false)
        )
    }

fun List<InstitutionOffer>.generateOfferDtoV2List(): List<OfferDtoV2> =
    this.map {
        OfferDtoV2(
            id = requireNotNull(it.id),
            title = it.title,
            description = it.text,
            type = it.offerType.name,
            photoUrl = it.pictureUrl,
            birthday = it.birthday,
            latestCheck = (it.updateEntityDateTime ?: it.createEntityDateTime)
                .let(::requireNotNull).let(ZonedDateTime::parse).toLocalDate().toString(),
            worksUpDtoList = it.workTime.map { x ->
                WorksUpDtoV1(
                    dayOfWeek = x.dayOfWeek.name,
                    startWork = x.startTime,
                    endWork = x.endTime,
                    closed = false,
                    always = x.startTime == "00:00" && x.endTime == "00:00"
                )
            },
            institutionDto = it.getInstitutionNotNull().generateInstitutionDtoV1()
        )
    }

fun InstitutionOffer.generateOfferDtoV2(): OfferDtoV2 =
    OfferDtoV2(
        id = requireNotNull(this.id),
        title = this.title,
        description = this.text,
        type = this.offerType.name,
        photoUrl = this.pictureUrl,
        birthday = this.birthday,
        latestCheck = (this.updateEntityDateTime ?: this.createEntityDateTime)
            ?.let(ZonedDateTime::parse)?.toLocalDate()?.toString() ?: "",
        worksUpDtoList = this.workTime.map { x ->
            WorksUpDtoV1(
                dayOfWeek = x.dayOfWeek.name,
                startWork = x.startTime,
                endWork = x.endTime,
                closed = false,
                always = x.startTime == "00:00" && x.endTime == "00:00"
            )
        },
        institutionDto = this.getInstitutionNotNull().generateInstitutionDtoV1()
    )

fun InstitutionOffer.generateOfferDtoV2(dayOfWeek: DayOfWeeks, saved: Boolean, rated: Boolean): OfferDtoV2 =
    OfferDtoV2(
        id = requireNotNull(this.id),
        title = this.title,
        description = this.text,
        type = this.offerType.name,
        photoUrl = this.pictureUrl,
        birthday = this.birthday,
        latestCheck = (this.updateEntityDateTime ?: this.createEntityDateTime)
            ?.let(ZonedDateTime::parse)?.toLocalDate()?.toString() ?: "",
        worksUpDtoList = this.workTime.map { x ->
            WorksUpDtoV1(
                dayOfWeek = x.dayOfWeek.name,
                startWork = x.startTime,
                endWork = x.endTime,
                closed = false,
                always = x.startTime == "00:00" && x.endTime == "00:00"
            )
        },
        institutionDto = this.getInstitutionNotNull().generateInstitutionDtoV1(dayOfWeek, null, saved, rated)
    )

fun List<EntityWrapper<InstitutionOffer>>.generateBlockOfferDtoV2List(dayOfWeek: DayOfWeeks): List<BlockOffersDtoV2> =
    OfferType.values().map { offerType ->
        val offers =
            this.filter { offerType == it.entity.offerType }.generateOfferDtoV2List(dayOfWeek).shuffled()
        BlockOffersDtoV2(
            title = offerType.toOfferTitle(),
            offers = offers,
            blockType = offerType.toBlockType().name,
            showAll = offers.size > 3
        )
    }