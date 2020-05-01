package com.wellcome.main.service.extentions.generators.api.common

import com.wellcome.main.dto.api.newDto.common.v1.BlockOffersDtoV1
import com.wellcome.main.dto.api.newDto.common.v1.BlockType
import com.wellcome.main.dto.api.newDto.common.v1.OfferDtoV1
import com.wellcome.main.entity.institution.DayOfWeeks
import com.wellcome.main.entity.institution.InstitutionOffer
import com.wellcome.main.entity.institution.OfferType
import com.wellcome.main.wrapper.Delegate
import com.wellcome.main.wrapper.EntityWrapper
import java.time.ZonedDateTime

fun List<InstitutionOffer>.generateOfferDtoV1List() =
    this.map(InstitutionOffer::generateOfferDtoV1)

fun InstitutionOffer.generateOfferDtoV1(): OfferDtoV1 =
    OfferDtoV1(
        id = requireNotNull(this.id),
        title = this.title,
        description = this.text,
        type = this.offerType.name,
        photoUrl = this.pictureUrl,
        institutionDto = this.getInstitutionNotNull().generateInstitutionDtoV1()
    )

fun InstitutionOffer.generateOfferDtoV1(dayOfWeek: DayOfWeeks, saved: Boolean, rated: Boolean): OfferDtoV1 =
    OfferDtoV1(
        id = requireNotNull(this.id),
        title = this.title,
        description = this.text,
        type = this.offerType.name,
        photoUrl = this.pictureUrl,
        institutionDto = this.getInstitutionNotNull().generateInstitutionDtoV1(dayOfWeek, null, saved, rated)
    )

fun List<EntityWrapper<InstitutionOffer>>.generateBlockOfferDtoV1List(dayOfWeek: DayOfWeeks): List<BlockOffersDtoV1> =
    OfferType.values().map { offerType ->
        val offers =
            this.filter { offerType == it.entity.offerType }.generateOfferDtoV1List(dayOfWeek).shuffled()
        BlockOffersDtoV1(
            title = offerType.toOfferTitle(),
            offers = offers,
            blockType = offerType.toBlockType().name,
            showAll = offers.size > 3
        )
    }

fun List<EntityWrapper<InstitutionOffer>>.generateOfferDtoV1List(dayOfWeek: DayOfWeeks): List<OfferDtoV1> =
    this.map {
        var userDelegate: Delegate.UserDelegate? = null

        it.delegates.forEach { delegate ->
            when (delegate) {
                is Delegate.UserDelegate -> userDelegate = delegate
            }
        }
        OfferDtoV1(
            id = requireNotNull(it.entity.id),
            title = it.entity.title,
            description = it.entity.text,
            type = it.entity.offerType.name,
            photoUrl = it.entity.pictureUrl,
            institutionDto = it.entity
                .getInstitutionNotNull()
                .generateInstitutionDtoV1(dayOfWeek, null,
                    userDelegate?.saved ?: false, userDelegate?.rated ?: false)
        )
    }

