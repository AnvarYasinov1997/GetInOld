package com.wellcome.main.service.extentions.generators.admin

import com.wellcome.main.dto.admin.common.OfferDto
import com.wellcome.main.dto.admin.common.WorksUpDto
import com.wellcome.main.entity.institution.DayOfWeeks
import com.wellcome.main.entity.institution.InstitutionOffer

fun List<InstitutionOffer>.generateOfferDtoList(): List<OfferDto> = this.map {
    OfferDto(
        id = it.id!!,
        title = it.title,
        description = it.text,
        isBirthday = it.birthday,
        startDate = it.startDate,
        endDate = it.endDate,
        offerType = it.offerType.name,
        worksUp = it.generateWorksUpDtoList()
    )
}

fun InstitutionOffer.generateOfferDto(): OfferDto =
    OfferDto(
        id = this.id!!,
        title = this.title,
        description = this.text,
        isBirthday = this.birthday,
        startDate = this.startDate,
        endDate = this.endDate,
        offerType = this.offerType.name,
        worksUp = this.generateWorksUpDtoList()
    )

fun InstitutionOffer.generateWorksUpDtoList(): List<WorksUpDto> =
    DayOfWeeks.values().map { day ->
        val currentWorkTime = this.workTime.firstOrNull { day == it.dayOfWeek }
        WorksUpDto(
            dayOfWeek = day.name,
            startWork = currentWorkTime?.startTime ?: "",
            endWork = currentWorkTime?.endTime ?: "",
            closed = currentWorkTime == null,
            always = false
        )
    }