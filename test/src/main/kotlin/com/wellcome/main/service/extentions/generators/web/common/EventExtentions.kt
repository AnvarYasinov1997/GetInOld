package com.wellcome.main.service.extentions.generators.web.common

import com.wellcome.main.dto.web.common.EventDto
import com.wellcome.main.entity.institution.DayOfWeeks
import com.wellcome.main.entity.institution.InstitutionEvent
import com.wellcome.main.util.functions.simplyfyDate
import com.wellcome.main.wrapper.Delegate
import com.wellcome.main.wrapper.EntityWrapper
import java.time.LocalDate

fun List<InstitutionEvent>.generateEventDtoList(): List<EventDto> =
    this.map(InstitutionEvent::generateEventDto)

fun InstitutionEvent.generateEventDto(): EventDto =
    EventDto(
        id = requireNotNull(this.id),
        title = this.title,
        pictureUrl = this.pictureUrl,
        description = this.description,
        startWork = this.startWork,
        startDate = LocalDate.parse(this.date).simplyfyDate(),
        square = this.square,
        price = this.price.toPriceString(),
        institutionDto = this.institution.generateInstitutionDto(),
        saved = false
    )

fun InstitutionEvent.generateEventDto(dayOfWeek: DayOfWeeks, saved: Boolean, rated: Boolean): EventDto =
    EventDto(
        id = requireNotNull(this.id),
        title = this.title,
        pictureUrl = this.pictureUrl,
        description = this.description,
        startWork = this.startWork,
        startDate = LocalDate.parse(this.date).simplyfyDate(),
        square = this.square,
        price = this.price.toPriceString(),
        institutionDto = this.institution.generateInstitutionDto(dayOfWeek, null, saved, rated),
        saved = false
    )

fun List<EntityWrapper<InstitutionEvent>>.generateEventDtoList(dayOfWeek: DayOfWeeks): List<EventDto> =
    this.map {
        var userDelegate: Delegate.UserDelegate? = null

        it.delegates.forEach { delegate ->
            when (delegate) {
                is Delegate.UserDelegate -> userDelegate = delegate
            }
        }
        EventDto(
            id = requireNotNull(it.entity.id),
            title = it.entity.title,
            pictureUrl = it.entity.pictureUrl,
            description = it.entity.description,
            startWork = it.entity.startWork,
            startDate = LocalDate.parse(it.entity.date).simplyfyDate(),
            square = it.entity.square,
            price = it.entity.price.toPriceString(),
            institutionDto = it.entity.institution.generateInstitutionDto(dayOfWeek, null,
                userDelegate?.saved ?: false, userDelegate?.rated ?: false),
            saved = false
        )
    }