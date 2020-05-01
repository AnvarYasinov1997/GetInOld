package com.wellcome.main.service.extentions.generators.admin

import com.wellcome.main.dto.admin.common.InstitutionEditRequestDto
import com.wellcome.main.dto.admin.response.PictureDto
import com.wellcome.main.entity.institutionProfile.*

fun InstitutionEditRequest.generateInstitutionEditRequestDto(): InstitutionEditRequestDto =
    InstitutionEditRequestDto(
        institutionEditRequestId = requireNotNull(this.id),
        status = this.status.status.name,
        developerMessage = this.status.developerMessage,
        avatarUrl = this.avatarUrl,
        description = this.description,
        newPictures = this.pictures
            .filter { it.status == InstitutionEditRequestContentStatus.ADD }
            .map(InstitutionEditRequestPicture::picture)
            .map { PictureDto(requireNotNull(it.id), it.pictureUrl) },
        deletePictures = this.pictures
            .filter { it.status == InstitutionEditRequestContentStatus.REMOVE }
            .map(InstitutionEditRequestPicture::picture)
            .map { PictureDto(requireNotNull(it.id), it.pictureUrl) },
        newOffers = this.offers
            .filter { it.status == InstitutionEditRequestContentStatus.ADD }
            .map(InstitutionEditRequestOffer::offer)
            .generateOfferDtoList(),
        deleteOffers = this.offers
            .filter { it.status == InstitutionEditRequestContentStatus.REMOVE }
            .map(InstitutionEditRequestOffer::offer)
            .generateOfferDtoList(),
        newEvents = this.events
            .filter { it.status == InstitutionEditRequestContentStatus.ADD }
            .map(InstitutionEditRequestEvent::event)
            .generateEventDtoList(),
        deleteEvents = this.events
            .filter { it.status == InstitutionEditRequestContentStatus.REMOVE }
            .map(InstitutionEditRequestEvent::event)
            .generateEventDtoList()
    )