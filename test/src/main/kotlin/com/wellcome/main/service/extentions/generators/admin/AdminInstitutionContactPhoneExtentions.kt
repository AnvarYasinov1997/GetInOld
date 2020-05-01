package com.wellcome.main.service.extentions.generators.admin

import com.wellcome.main.dto.admin.response.PhoneDto
import com.wellcome.main.entity.institution.InstitutionContactPhone

fun List<InstitutionContactPhone>.generatePhoneDtoList(): List<PhoneDto> = this.map {
    PhoneDto(
        id = requireNotNull(it.id),
        phoneNumber = it.phoneNumber
    )
}