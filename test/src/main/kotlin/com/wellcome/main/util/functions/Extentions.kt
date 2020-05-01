package com.wellcome.main.util.functions

import com.wellcome.main.dto.moderation.common.v1.AnalyticConversionTypeDto
import com.wellcome.main.dto.moderation.common.v1.AnalyticEventDtoType
import com.wellcome.main.entity.institution.DayOfWeeks
import com.wellcome.main.entity.institution.InstitutionCategoryType
import com.wellcome.main.util.variables.MobileAnalyticEvent
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month

fun <T> List<T>.ifNotEmpty(): List<T>? =
    if (isEmpty()) null else this

infix fun <T> List<T>.merge(list: List<T>): List<T> = this.union(list).toMutableList()

fun String?.toInstagramLink(): String? {
    if (this == null || this.isEmpty()) return null
    if (this.startsWith("https://instagram.com", false)) return this
    if (this.startsWith("https://www.instagram.com", false)) return this
    if (this.startsWith("@")) return "https://instagram.com/${this.substring(1)}"
    return "https://instagram.com/$this"
}

fun DayOfWeek.toDayOfWeek(): DayOfWeeks {
    return when (this) {
        DayOfWeek.MONDAY -> DayOfWeeks.MONDAY
        DayOfWeek.TUESDAY -> DayOfWeeks.TUESDAY
        DayOfWeek.WEDNESDAY -> DayOfWeeks.WEDNESDAY
        DayOfWeek.THURSDAY -> DayOfWeeks.THURSDAY
        DayOfWeek.FRIDAY -> DayOfWeeks.FRIDAY
        DayOfWeek.SATURDAY -> DayOfWeeks.SATURDAY
        DayOfWeek.SUNDAY -> DayOfWeeks.SUNDAY
        else -> throw Exception("There are seven days in a week")
    }
}

fun LocalDate.simplyfyDate(): String =
    "${this.dayOfMonth} ${this.month.translateMonthToRussian()}"

fun Month.translateMonthToRussian(): String =
    when (this) {
        Month.JANUARY -> "Января"
        Month.FEBRUARY -> "Февраля"
        Month.MARCH -> "Марта"
        Month.APRIL -> "Апреля"
        Month.MAY -> "Мая"
        Month.JUNE -> "Июня"
        Month.JULY -> "Июля"
        Month.AUGUST -> "Августа"
        Month.SEPTEMBER -> "Сентября"
        Month.OCTOBER -> "Октября"
        Month.NOVEMBER -> "Ноября"
        Month.DECEMBER -> "Декабря"
    }

fun Double.calculateDistance(): Double =
    this.let(MathSupport::divisionFive)
        .let(MathSupport::multiplicationSixty)
        .let {
            it.plus(this
                .let(MathSupport::divisionHundred)
                .let(MathSupport::multiplicationTwenty)
                .let(MathSupport::divisionFive)
                .let(MathSupport::multiplicationSixty))
        }

fun DayOfWeeks.toTimesTitle(): String =
    when (this) {
        DayOfWeeks.MONDAY -> "В понедельник"
        DayOfWeeks.TUESDAY -> "Во вторник"
        DayOfWeeks.WEDNESDAY -> "В среду"
        DayOfWeeks.THURSDAY -> "В четверг"
        DayOfWeeks.FRIDAY -> "В пятницу"
        DayOfWeeks.SATURDAY -> "В субботу"
        else -> "В воскресенье"
    }

fun Throwable.getMessage() = message.orEmpty()

fun String.plusCategoryCompletion(category: InstitutionCategoryType): String {
    return when (category) {
        InstitutionCategoryType.HOOKAH -> "$this кальянные"
        InstitutionCategoryType.NIGHT_CLUB -> "$this клубы"
        InstitutionCategoryType.STRIP_BAR -> "$this стрип бары"
        InstitutionCategoryType.KARAOKE -> "$this караоке"
        InstitutionCategoryType.PUB -> "$this пабы"
        InstitutionCategoryType.BAR -> "$this бары"
        InstitutionCategoryType.LOUNGE_BAR -> "$this лаундж бары"
        InstitutionCategoryType.RESTAURANT -> "$this рестораны"
        InstitutionCategoryType.COFFEE_HOUSE -> "$this кофейни"
        InstitutionCategoryType.VAPE_BAR -> "$this вейп бары"
    }
}

fun String.plusCategoryCompletionV2(category: InstitutionCategoryType): String {
    return when (category) {
        InstitutionCategoryType.HOOKAH -> "$this в кальянных"
        InstitutionCategoryType.NIGHT_CLUB -> "$this в клубах"
        InstitutionCategoryType.STRIP_BAR -> "$this в стрип барах"
        InstitutionCategoryType.KARAOKE -> "$this в караоке"
        InstitutionCategoryType.PUB -> "$this в пабах"
        InstitutionCategoryType.BAR -> "$this в барах"
        InstitutionCategoryType.LOUNGE_BAR -> "$this в лаундж барах"
        InstitutionCategoryType.RESTAURANT -> "$this в ресторанах"
        InstitutionCategoryType.COFFEE_HOUSE -> "$this в кофейнях"
        InstitutionCategoryType.VAPE_BAR -> "$this в вейп барах"
    }
}