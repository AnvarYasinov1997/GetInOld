package com.wellcome.main.service.extentions.generators.admin

import com.wellcome.main.dto.admin.common.EventDto
import com.wellcome.main.entity.Price
import com.wellcome.main.entity.institution.InstitutionEvent

fun List<InstitutionEvent>.generateEventDtoList(): List<EventDto> = this.map {
    EventDto(id = it.id!!,
        title = it.title,
        pictureUrl = it.pictureUrl,
        description = it.description,
        startWork = it.startWork,
        date = it.date,
        square = it.square,
        price = it.price.let(Price::generatePriceDto)
    )
}