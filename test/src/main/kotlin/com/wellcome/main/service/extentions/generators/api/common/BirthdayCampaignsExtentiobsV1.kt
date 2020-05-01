package com.wellcome.main.service.extentions.generators.api.common

import com.wellcome.main.dto.api.newDto.common.v1.BirthdayCampaignDtoV1
import com.wellcome.main.entity.institution.BirthdayCampaign

fun List<BirthdayCampaign>.generateBirthdayCampaignDtoV1List() =
    this.map {
        BirthdayCampaignDtoV1(
            id = it.id!!,
            age = it.age,
            text = it.text,
            gender = it.gender.name,
            status = it.status.name,
            expirationDate = it.expirationDate.toString(),
            creationDate = requireNotNull(it.createEntityDateTime),
            institutionDto = it.institution.generateInstitutionDtoV1()
        )
    }