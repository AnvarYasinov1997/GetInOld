package com.wellcome.main.service.extentions.generators.api.common

import com.wellcome.main.dto.api.newDto.common.v1.BlockEventDtoV1
import com.wellcome.main.dto.api.newDto.common.v1.EventDtoV1
import com.wellcome.main.entity.institution.DayOfWeeks
import com.wellcome.main.entity.institution.InstitutionEvent
import com.wellcome.main.util.functions.simplyfyDate
import com.wellcome.main.wrapper.Delegate
import com.wellcome.main.wrapper.EntityWrapper
import java.time.LocalDate
import java.time.ZonedDateTime

fun Map<String, List<EntityWrapper<InstitutionEvent>>>.generateBlockEventDtoV1List(dayOfWeek: DayOfWeeks): List<BlockEventDtoV1> =
    this.map {
        BlockEventDtoV1(
            title = it.key,
            events = it.value.generateEventDtoV1List(dayOfWeek)
        )
    }

fun List<InstitutionEvent>.generateEventDtoV1List() =
    this.map(InstitutionEvent::generateEventDtoV1)

fun InstitutionEvent.generateEventDtoV1(): EventDtoV1 =
    EventDtoV1(
        id = requireNotNull(this.id),
        title = this.title,
        pictureUrl = this.pictureUrl,
        description = this.description,
        startWork = this.startWork,
        startDate = LocalDate.parse(this.date).simplyfyDate(),
        square = this.square,
        price = this.price.toPriceString(),
        institutionDto = this.institution.generateInstitutionDtoV1(),
        saved = false
    )

fun InstitutionEvent.generateEventDtoV1(dayOfWeek: DayOfWeeks, saved: Boolean, rated: Boolean): EventDtoV1 =
    EventDtoV1(
        id = requireNotNull(this.id),
        title = this.title,
        pictureUrl = this.pictureUrl,
        description = this.description,
        startWork = this.startWork,
        startDate = LocalDate.parse(this.date).simplyfyDate(),
        square = this.square,
        price = this.price.toPriceString(),
        institutionDto = this.institution.generateInstitutionDtoV1(dayOfWeek, null, saved, rated),
        saved = false
    )

fun List<EntityWrapper<InstitutionEvent>>.generateEventDtoV1List(dayOfWeek: DayOfWeeks): List<EventDtoV1> =
    this.map {
        var userDelegate: Delegate.UserDelegate? = null

        it.delegates.forEach { delegate ->
            when (delegate) {
                is Delegate.UserDelegate -> userDelegate = delegate
            }
        }
        EventDtoV1(
            id = requireNotNull(it.entity.id),
            title = it.entity.title,
            pictureUrl = it.entity.pictureUrl,
            description = it.entity.description,
            startWork = it.entity.startWork,
            startDate = LocalDate.parse(it.entity.date).simplyfyDate(),
            square = it.entity.square,
            price = it.entity.price.toPriceString(),
            institutionDto = it.entity.institution.generateInstitutionDtoV1(dayOfWeek, null,
                userDelegate?.saved ?: false, userDelegate?.rated ?: false),
            saved = false
        )
    }

