package com.wellcome.main.service.extentions.generators.api.common

import com.wellcome.main.dto.api.newDto.common.v1.TimeDtoV1
import com.wellcome.main.dto.api.newDto.response.v1.OfferWorkDayAttributeDtoV1
import com.wellcome.main.entity.institution.DayOfWeeks
import com.wellcome.main.util.enumerators.SearchInstitutionDays
import com.wellcome.main.util.functions.toDayOfWeek
import com.wellcome.main.util.functions.toTimesTitle
import java.time.ZonedDateTime

fun generateTimesDtoV1List(userZonedDateTime: ZonedDateTime): List<TimeDtoV1> {
    var currentWeek = userZonedDateTime.dayOfWeek.toDayOfWeek()
    val times = mutableListOf<TimeDtoV1>()
    times.add(TimeDtoV1("Сейчас", true, SearchInstitutionDays.NOW.name))
    for (i in 1..DayOfWeeks.values().size) {
        when (i) {
            1 -> times.add(TimeDtoV1("Сегодня", true, SearchInstitutionDays.TODAY.name))
            2 -> times.add(TimeDtoV1("Завтра", true, SearchInstitutionDays.TOMORROW.name))
            else -> times.add(TimeDtoV1(currentWeek.toTimesTitle(), false, currentWeek.name))
        }
        currentWeek = currentWeek.incrementDayOfWeek()
    }
    return times
}

fun generateOfferWorkDayAttributesDtoV1List(userZonedDateTime: ZonedDateTime): List<OfferWorkDayAttributeDtoV1> {
    var currentWeek = userZonedDateTime.dayOfWeek.toDayOfWeek()
    val attributes = mutableListOf<OfferWorkDayAttributeDtoV1>()
    attributes.add(OfferWorkDayAttributeDtoV1("На день рождения", true, SearchInstitutionDays.BIRTHDAY.name))
    for (i in 1..DayOfWeeks.values().size) {
        when (i) {
            1 -> attributes.add(OfferWorkDayAttributeDtoV1("Сегодня", true, SearchInstitutionDays.TODAY.name))
            2 -> attributes.add(OfferWorkDayAttributeDtoV1("Завтра", true, SearchInstitutionDays.TOMORROW.name))
            else -> attributes.add(OfferWorkDayAttributeDtoV1(currentWeek.toTimesTitle(), false, currentWeek.name))
        }
        currentWeek = currentWeek.incrementDayOfWeek()
    }
    return attributes
}