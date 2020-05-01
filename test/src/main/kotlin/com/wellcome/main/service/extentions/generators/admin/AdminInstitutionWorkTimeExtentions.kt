package com.wellcome.main.service.extentions.generators.admin

import com.wellcome.main.dto.admin.common.WorksUpDto
import com.wellcome.main.entity.institution.InstitutionWorkTime

fun List<InstitutionWorkTime>.generateWorksUpDtoList(): List<WorksUpDto> = this.map {
    WorksUpDto(
        dayOfWeek = it.dayOfWeek.name,
        startWork = it.startDay,
        endWork = it.endDay,
        closed = it.closed,
        always = it.startDay == "00:00" && it.endDay == "00:00"
    )
}