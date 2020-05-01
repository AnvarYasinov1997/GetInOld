package com.wellcome.main.service.extentions.generators.web.common

import com.wellcome.main.dto.web.common.BirthdayCampaignDto
import com.wellcome.main.entity.institution.BirthdayCampaign

fun List<BirthdayCampaign>.generateBirthdayCampaignDtoList(): List<BirthdayCampaignDto> =
    this.map {
        BirthdayCampaignDto(
            id = it.id!!,
            age = it.age,
            text = it.text,
            gender = it.gender.name,
            status = it.status.name,
            expirationDate = it.expirationDate.toString(),
            creationDate = requireNotNull(it.createEntityDateTime),
            institutionDto = it.institution.generateInstitutionDto()
        )
    }