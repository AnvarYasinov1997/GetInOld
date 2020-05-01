package com.wellcome.main.service.extentions.generators.web.common

import com.wellcome.main.dto.web.common.BlockOffersDto
import com.wellcome.main.dto.web.common.OfferDto
import com.wellcome.main.dto.web.common.WorksUpDto
import com.wellcome.main.entity.institution.DayOfWeeks
import com.wellcome.main.entity.institution.InstitutionOffer
import com.wellcome.main.entity.institution.OfferType
import com.wellcome.main.wrapper.Delegate
import com.wellcome.main.wrapper.EntityWrapper
import java.time.ZonedDateTime

fun List<EntityWrapper<InstitutionOffer>>.generateOfferDtoList(dayOfWeek: DayOfWeeks): List<OfferDto> =
    this.map {
        var userDelegate: Delegate.UserDelegate? = null

        it.delegates.forEach { delegate ->
            when (delegate) {
                is Delegate.UserDelegate -> userDelegate = delegate
            }
        }
        OfferDto(
            id = requireNotNull(it.entity.id),
            title = it.entity.title,
            description = it.entity.text,
            type = it.entity.offerType.name,
            photoUrl = it.entity.pictureUrl,
            birthday = it.entity.birthday,
            latestCheck = (it.entity.updateEntityDateTime ?: it.entity.createEntityDateTime)
                ?.let(ZonedDateTime::parse)?.toLocalDate()?.toString() ?: "",
            worksUpDtoList = it.entity.workTime.map { x ->
                WorksUpDto(
                    dayOfWeek = x.dayOfWeek.name,
                    startWork = x.startTime,
                    endWork = x.endTime,
                    closed = false,
                    always = x.startTime == "00:00" && x.endTime == "00:00"
                )
            },
            institutionDto = it.entity
                .getInstitutionNotNull()
                .generateInstitutionDto(dayOfWeek, null,
                    userDelegate?.saved ?: false, userDelegate?.rated ?: false)
        )
    }

fun List<InstitutionOffer>.generateOfferDtoList(): List<OfferDto> =
    this.map {
        OfferDto(
            id = requireNotNull(it.id),
            title = it.title,
            description = it.text,
            type = it.offerType.name,
            photoUrl = it.pictureUrl,
            birthday = it.birthday,
            latestCheck = (it.updateEntityDateTime ?: it.createEntityDateTime)
                .let(::requireNotNull).let(ZonedDateTime::parse).toLocalDate().toString(),
            worksUpDtoList = it.workTime.map { x ->
                WorksUpDto(
                    dayOfWeek = x.dayOfWeek.name,
                    startWork = x.startTime,
                    endWork = x.endTime,
                    closed = false,
                    always = x.startTime == "00:00" && x.endTime == "00:00"
                )
            },
            institutionDto = it.getInstitutionNotNull().generateInstitutionDto()
        )
    }

fun InstitutionOffer.generateOfferDto(): OfferDto =
    OfferDto(
        id = requireNotNull(this.id),
        title = this.title,
        description = this.text,
        type = this.offerType.name,
        photoUrl = this.pictureUrl,
        birthday = this.birthday,
        latestCheck = (this.updateEntityDateTime ?: this.createEntityDateTime)
            ?.let(ZonedDateTime::parse)?.toLocalDate()?.toString() ?: "",
        worksUpDtoList = this.workTime.map { x ->
            WorksUpDto(
                dayOfWeek = x.dayOfWeek.name,
                startWork = x.startTime,
                endWork = x.endTime,
                closed = false,
                always = x.startTime == "00:00" && x.endTime == "00:00"
            )
        },
        institutionDto = this.getInstitutionNotNull().generateInstitutionDto()
    )

fun InstitutionOffer.generateOfferDto(dayOfWeek: DayOfWeeks, saved: Boolean, rated: Boolean): OfferDto =
    OfferDto(
        id = requireNotNull(this.id),
        title = this.title,
        description = this.text,
        type = this.offerType.name,
        photoUrl = this.pictureUrl,
        birthday = this.birthday,
        latestCheck = (this.updateEntityDateTime ?: this.createEntityDateTime)
            ?.let(ZonedDateTime::parse)?.toLocalDate()?.toString() ?: "",
        worksUpDtoList = this.workTime.map { x ->
            WorksUpDto(
                dayOfWeek = x.dayOfWeek.name,
                startWork = x.startTime,
                endWork = x.endTime,
                closed = false,
                always = x.startTime == "00:00" && x.endTime == "00:00"
            )
        },
        institutionDto = this.getInstitutionNotNull().generateInstitutionDto(dayOfWeek, null, saved, rated)
    )

fun List<EntityWrapper<InstitutionOffer>>.generateBlockOfferDtoList(dayOfWeek: DayOfWeeks): List<BlockOffersDto> =
    OfferType.values().map { offerType ->
        val offers =
            this.filter { offerType == it.entity.offerType }.generateOfferDtoList(dayOfWeek).shuffled()
        BlockOffersDto(
            title = offerType.toOfferTitle(),
            offers = offers,
            blockType = offerType.toBlockType().name,
            showAll = offers.size > 3
        )
    }