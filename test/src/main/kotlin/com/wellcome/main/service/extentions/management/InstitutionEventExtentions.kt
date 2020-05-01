package com.wellcome.main.service.extentions.management

import com.wellcome.main.entity.institution.InstitutionEvent
import com.wellcome.main.util.functions.toDayOfWeek
import com.wellcome.main.util.functions.toTimesTitle
import java.time.LocalDate
import java.time.ZonedDateTime

fun List<InstitutionEvent>.filterFutureWeek(userZonedDateTime: ZonedDateTime): List<InstitutionEvent> {
    val futureDate = userZonedDateTime.toLocalDate().plusDays(5)
    return this.filter {
        LocalDate.parse(it.date) <= futureDate
    }
}

fun List<InstitutionEvent>.filterNotFutureWeek(userZonedDateTime: ZonedDateTime): List<InstitutionEvent> {
    val futureDate = userZonedDateTime.toLocalDate().plusDays(5)
    return this.filterNot {
        LocalDate.parse(it.date) <= futureDate
    }
}

fun List<InstitutionEvent>.sortByDayOfWeeks(): Map<String, MutableList<InstitutionEvent>> {
    val sortedEvents = mutableMapOf<String, MutableList<InstitutionEvent>>()
    for (i in this.sortedBy { LocalDate.parse(it.date) }) {
        val dayOfWeek = LocalDate.parse(i.date).dayOfWeek.toDayOfWeek().toTimesTitle()
        if (sortedEvents[dayOfWeek] == null) {
            sortedEvents[dayOfWeek] = mutableListOf(i)
        } else {
            requireNotNull(sortedEvents[dayOfWeek]).add(i)
        }
    }
    return sortedEvents
}