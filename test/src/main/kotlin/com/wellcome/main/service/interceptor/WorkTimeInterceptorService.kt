package com.wellcome.main.service.interceptor

import com.wellcome.main.entity.institution.*
import com.wellcome.main.service.utils.LogMessage
import com.wellcome.main.service.utils.LoggerService
import com.wellcome.main.util.enumerators.SearchInstitutionDays
import com.wellcome.main.util.functions.tryOrFalse
import com.wellcome.main.wrapper.Delegate
import com.wellcome.main.wrapper.EntityWrapper
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.*

interface WorkTimeInterceptorService {
    fun handleWorkTimeInstitutions(day: String, userZonedDateTime: ZonedDateTime, wrappers: List<EntityWrapper<Institution>>): List<EntityWrapper<Institution>>
    fun handleWorkTimeOffers(day: String, userZonedDateTime: ZonedDateTime, wrappers: List<EntityWrapper<InstitutionOffer>>): List<EntityWrapper<InstitutionOffer>>
    fun handleWorkTimeEvents(day: String, userZonedDateTime: ZonedDateTime, wrappers: List<EntityWrapper<InstitutionEvent>>): List<EntityWrapper<InstitutionEvent>>
}

@Service
open class DefaultWorkTimeInterceptorService @Autowired constructor(
    private val loggerService: LoggerService
) : WorkTimeInterceptorService {

    var localDate: LocalDate? = null // Для тестов

    override fun handleWorkTimeInstitutions(day: String, userZonedDateTime: ZonedDateTime, wrappers: List<EntityWrapper<Institution>>): List<EntityWrapper<Institution>> {
        try {
            val searchDay = SearchInstitutionDays.valueOf(day)
            if (searchDay == SearchInstitutionDays.BIRTHDAY) throw Exception("Method birthday not supported")
            if (searchDay == SearchInstitutionDays.NOW) {
                return wrappers.map {
                    if (this.isInstitutionWorkingNow(it.entity, userZonedDateTime))
                        it.apply { this.delegates.add(Delegate.TimeDelegate(true)) }
                    else it.apply { this.delegates.add(Delegate.TimeDelegate(false)) }
                    return@map it
                }
            }
            if (searchDay == SearchInstitutionDays.TODAY) {
                return wrappers.map {
                    if (this.isInstitutionWorkingToday(it.entity, userZonedDateTime)
                        || this.isInstitutionWorkingNow(it.entity, userZonedDateTime))
                        it.apply { this.delegates.add(Delegate.TimeDelegate(true)) }
                    else it.apply { this.delegates.add(Delegate.TimeDelegate(false)) }
                    return@map it
                }
            }
            if (searchDay == SearchInstitutionDays.TOMORROW) {
                return wrappers.map {
                    if (this.isInstitutionWorkingByDay(it.entity, 1, userZonedDateTime))
                        it.apply { this.delegates.add(Delegate.TimeDelegate(true)) }
                    else it.apply { this.delegates.add(Delegate.TimeDelegate(false)) }
                    return@map it
                }
            }
        } catch (e: IllegalArgumentException) {
            return wrappers.map {
                if (this.isInstitutionWorkingByDayOfWeek(it.entity, DayOfWeeks.valueOf(day)))
                    it.apply { this.delegates.add(Delegate.TimeDelegate(true)) }
                else it.apply { this.delegates.add(Delegate.TimeDelegate(false)) }
                return@map it
            }
        }
        return wrappers
    }

    override fun handleWorkTimeOffers(day: String, userZonedDateTime: ZonedDateTime, wrappers: List<EntityWrapper<InstitutionOffer>>): List<EntityWrapper<InstitutionOffer>> {
        try {
            val searchDay = SearchInstitutionDays.valueOf(day)
            if (searchDay == SearchInstitutionDays.NOW)
                return wrappers.map {
                    if (this.isInstitutionWorkingNow(it.entity.getInstitutionNotNull(), userZonedDateTime)
                        && this.isOfferWorkingNow(it.entity, userZonedDateTime)
                        && !it.entity.birthday)
                        it.apply { this.delegates.add(Delegate.TimeDelegate(true)) }
                    else it.apply { this.delegates.add(Delegate.TimeDelegate(false)) }
                    return@map it
                }
            if (searchDay == SearchInstitutionDays.TODAY) {
                return wrappers.map {
                    if ((this.isInstitutionWorkingToday(it.entity.getInstitutionNotNull(), userZonedDateTime)
                            || this.isInstitutionWorkingNow(it.entity.getInstitutionNotNull(), userZonedDateTime))
                        && (this.isOfferWorkingToday(it.entity, userZonedDateTime)
                            || this.isOfferWorkingNow(it.entity, userZonedDateTime))
                        && !it.entity.birthday)
                        it.apply { this.delegates.add(Delegate.TimeDelegate(true)) }
                    else it.apply { this.delegates.add(Delegate.TimeDelegate(false)) }
                    return@map it
                }
            }
            if (searchDay == SearchInstitutionDays.TOMORROW) {
                return wrappers.map {
                    if (this.isOfferWorkingByDay(it.entity, 1, userZonedDateTime) && !it.entity.birthday)
                        it.apply { this.delegates.add(Delegate.TimeDelegate(true)) }
                    else it.apply { this.delegates.add(Delegate.TimeDelegate(false)) }
                    return@map it
                }
            }
            if (searchDay == SearchInstitutionDays.BIRTHDAY) {
                return wrappers.map {
                    if (it.entity.birthday) it.apply { this.delegates.add(Delegate.TimeDelegate(true)) }
                    else it.apply { this.delegates.add(Delegate.TimeDelegate(false)) }
                    return@map it
                }
            }
        } catch (e: IllegalArgumentException) {
            return wrappers.map {
                if (this.isOfferWorkingByDayOfWeek(it.entity, DayOfWeeks.valueOf(day)) && !it.entity.birthday)
                    it.apply { this.delegates.add(Delegate.TimeDelegate(true)) }
                else it.apply { this.delegates.add(Delegate.TimeDelegate(false)) }
                return@map it
            }
        }
        return wrappers
    }

    override fun handleWorkTimeEvents(day: String, userZonedDateTime: ZonedDateTime, wrappers: List<EntityWrapper<InstitutionEvent>>): List<EntityWrapper<InstitutionEvent>> {
        val timeZoneId = userZonedDateTime.zone.toString()
        try {
            if (SearchInstitutionDays.valueOf(day) == SearchInstitutionDays.NOW) return wrappers.map {
                if (LocalDate.parse(it.entity.date) == userZonedDateTime.toLocalDate()
                    && convertToLocalDateTime(it.entity.startWork, timeZoneId) < userZonedDateTime.toLocalDateTime()) {
                    it.apply { this.delegates.add(Delegate.TimeDelegate(true)) }
                } else it.apply { this.delegates.add(Delegate.TimeDelegate(false)) }
                return@map it
            }
            throw IllegalArgumentException()
        } catch (e: IllegalArgumentException) {
            val stepCount = getStepsByDayOfWeek(day, userZonedDateTime)
            return wrappers.map {
                if (LocalDate.parse(it.entity.date) == userZonedDateTime.plusDays(stepCount).toLocalDate()) it.apply { this.delegates.add(Delegate.TimeDelegate(true)) }
                else it.apply { this.delegates.add(Delegate.TimeDelegate(false)) }
                return@map it
            }
        }
    }

    fun isInstitutionWorkingNow(institution: Institution, userZonedDateTime: ZonedDateTime): Boolean {
        val timeZoneId = userZonedDateTime.zone.toString()
        val currentDayOfWeek = userZonedDateTime.dayOfWeek.toDayOfWeek()
        val workDay = institution.workTime.getByDayOfWeek(currentDayOfWeek)
        val lastWorkDay = institution.workTime.getByDayOfWeek(currentDayOfWeek.decrementDayOfWeek())
        val endIsNextDay = tryOrFalse {
            return@tryOrFalse if (workDay.startDay != workDay.endDay && workDay.endDay == "00:00") false
            else this.convertToLocalDateTime(workDay.endDay, timeZoneId) <= this.convertToLocalDateTime(workDay.startDay, timeZoneId)
        }
        val lastDayEndIsNextDay = tryOrFalse {
            return@tryOrFalse if (lastWorkDay.endDay == "00:00") false
            else this.convertToLocalDateTime(lastWorkDay.endDay, timeZoneId) <= this.convertToLocalDateTime("12:00", timeZoneId)
        }
        return this.checkWork(
            startDay = workDay.startDay,
            endDay = workDay.endDay,
            closed = workDay.closed,
            endIsNextDay = endIsNextDay,
            lastEndDay = lastWorkDay.endDay,
            lastDayClosed = lastWorkDay.closed,
            lastDayEndIsNextDay = lastDayEndIsNextDay,
            userZonedDateTime = userZonedDateTime)
    }

    fun isOfferWorkingNow(offer: InstitutionOffer, userZonedDateTime: ZonedDateTime): Boolean {
        val timeZoneId = userZonedDateTime.zone.toString()
        val currentDayOfWeek = userZonedDateTime.dayOfWeek.toDayOfWeek()
        val workTime = offer.workTime.getByDayOfWeek(currentDayOfWeek)
        val lastWorkTime = offer.workTime.getByDayOfWeek(currentDayOfWeek.decrementDayOfWeek())
        val endIsNextDay = workTime?.let {
            return@let if (it.startTime != it.endTime && it.endTime == "00:00") false
            else this.convertToLocalDateTime(it.endTime, timeZoneId) <= this.convertToLocalDateTime("12:00", timeZoneId)
        } ?: false
        val lastDayEndIsNextDay = tryOrFalse {
            return@tryOrFalse if (requireNotNull(lastWorkTime).endTime == "00:00") false
            else this.convertToLocalDateTime(requireNotNull(lastWorkTime).endTime, timeZoneId) <= this.convertToLocalDateTime(lastWorkTime.startTime, timeZoneId)
        }
        return this.checkWork(
            startDay = workTime?.startTime ?: "",
            endDay = workTime?.endTime ?: "",
            closed = workTime == null,
            endIsNextDay = endIsNextDay,
            lastEndDay = lastWorkTime?.endTime ?: "",
            lastDayClosed = lastWorkTime == null,
            lastDayEndIsNextDay = lastDayEndIsNextDay,
            userZonedDateTime = userZonedDateTime)
    }

    private fun checkWork(startDay: String, endDay: String, closed: Boolean, endIsNextDay: Boolean,
                          lastEndDay: String, lastDayClosed: Boolean, lastDayEndIsNextDay: Boolean,
                          userZonedDateTime: ZonedDateTime): Boolean {
        val timeZoneId = userZonedDateTime.zone.toString()
        val currentDateTime = userZonedDateTime.toLocalDateTime()
        val startDayDateTime = this.convertToLocalDateTimeOrNull(startDay, timeZoneId)
        val endDayDateTime =
            if (startDay != endDay && endDay == "00:00") this.convertToLocalDateTimeOrNull("23:59", timeZoneId)
            else this.convertToLocalDateTimeOrNull(endDay, timeZoneId)
        val endLastDayDateTime =
            if (lastEndDay == "00:00") this.convertToLocalDateTimeOrNull("23:59", timeZoneId)
            else this.convertToLocalDateTimeOrNull(lastEndDay, timeZoneId)
        if (!this.validateFields(startDayDateTime, endDayDateTime, closed, endLastDayDateTime, lastDayClosed)) {
            loggerService.sendLogDeveloper(LogMessage("Institution or institution offer work time data is corrupted!"))
            return false
        }
        if (!closed)
            if (startDayDateTime == endDayDateTime)
                return true
        if (!lastDayClosed)
            if (lastDayEndIsNextDay)
                if (currentDateTime < endLastDayDateTime)
                    return true
        if (closed)
            return false
        if (currentDateTime < startDayDateTime)
            return false
        if (endIsNextDay)
            return true
        return currentDateTime < endDayDateTime
    }

    private fun validateFields(startDayDateTime: LocalDateTime?, endDayDateTime: LocalDateTime?, closed: Boolean,
                               endLastDayDateTime: LocalDateTime?, lastDayClosed: Boolean): Boolean {
        if (!closed) {
            startDayDateTime ?: return false
            endDayDateTime ?: return false
        }
        if (!lastDayClosed) {
            endLastDayDateTime ?: return false
        }
        return true
    }

    private fun isInstitutionWorkingToday(institution: Institution, userZonedDateTime: ZonedDateTime): Boolean {
        val timeZoneId = userZonedDateTime.zone.toString()
        val currentDayOfWeek = userZonedDateTime.dayOfWeek.toDayOfWeek()
        val workTime = institution.workTime.getByDayOfWeek(currentDayOfWeek)
        val currentDateTime = userZonedDateTime.toLocalDateTime()
        if (workTime.closed) return false
        val startDateTime = this.convertToLocalDateTime(workTime.startDay, timeZoneId)
        return currentDateTime < startDateTime
    }

    private fun isOfferWorkingToday(offer: InstitutionOffer, userZonedDateTime: ZonedDateTime): Boolean {
        val timeZoneId = userZonedDateTime.zone.toString()
        val currentDayOfWeek = userZonedDateTime.dayOfWeek.toDayOfWeek()
        val workTime = offer.workTime.getByDayOfWeek(currentDayOfWeek) ?: return false
        val currentDateTime = userZonedDateTime.toLocalDateTime()
        val startDateTime = this.convertToLocalDateTime(workTime.startTime, timeZoneId)
        return currentDateTime < startDateTime
    }

    private fun isInstitutionWorkingByDay(institution: Institution, dayCount: Long, userZonedDateTime: ZonedDateTime): Boolean {
        var currentDayOfWeek = userZonedDateTime.dayOfWeek.toDayOfWeek()
        for (i in 0 until dayCount) {
            currentDayOfWeek = currentDayOfWeek.incrementDayOfWeek()
        }
        val workTime = institution.workTime.getByDayOfWeek(currentDayOfWeek)
        return !workTime.closed
    }

    private fun isOfferWorkingByDay(offer: InstitutionOffer, dayCount: Long, userZonedDateTime: ZonedDateTime): Boolean {
        var currentDayOfWeek = userZonedDateTime.dayOfWeek.toDayOfWeek()
        for (i in 0 until dayCount) {
            currentDayOfWeek = currentDayOfWeek.incrementDayOfWeek()
        }
        val workTime = offer.workTime.getByDayOfWeek(currentDayOfWeek)
        return workTime != null
    }

    private fun isInstitutionWorkingByDayOfWeek(institution: Institution, dayOfWeek: DayOfWeeks): Boolean {
        return !institution.workTime.getByDayOfWeek(dayOfWeek).closed
    }

    private fun isOfferWorkingByDayOfWeek(offer: InstitutionOffer, dayOfWeek: DayOfWeeks): Boolean {
        return offer.workTime.getByDayOfWeek(dayOfWeek) != null
    }

    private fun List<InstitutionWorkTime>.getByDayOfWeek(dayOfWeek: DayOfWeeks): InstitutionWorkTime =
        this.first { it.dayOfWeek == dayOfWeek }

    private fun List<InstitutionOfferWorkTime>.getByDayOfWeek(dayOfWeek: DayOfWeeks): InstitutionOfferWorkTime? =
        this.firstOrNull { it.dayOfWeek == dayOfWeek }

    private fun convertToLocalDateTimeOrNull(timeString: String, timeZoneId: String): LocalDateTime? = try {
        this.convertToLocalDateTime(timeString, timeZoneId)
    } catch (e: Exception) {
        null
    }

    private fun convertToLocalDateTime(timeString: String, timeZoneId: String): LocalDateTime {
        val zoneId = ZoneId.of(timeZoneId)
        val ldt = this.localDate ?: LocalDate.now(zoneId) // Для тестов проверка на налл
        return ldt.atTime(StringUtils.left(timeString, 2).toInt(), StringUtils.right(timeString, 2).toInt())
    }

    private fun DayOfWeek.toDayOfWeek(): DayOfWeeks {
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

    private fun getStepsByDayOfWeek(searchDay: String, userZonedDateTime: ZonedDateTime): Long {
        return try {
            val day = SearchInstitutionDays.valueOf(searchDay)
            if (day == SearchInstitutionDays.NOW
                || day == SearchInstitutionDays.TODAY) return 0
            if (day == SearchInstitutionDays.TOMORROW) return 1
            throw Exception("Birthday is not a day")
        } catch (e: IllegalArgumentException) {
            val day = DayOfWeeks.valueOf(searchDay)
            val currentDayOfWeek = userZonedDateTime.dayOfWeek.toDayOfWeek()
            differenceBetweenDatOfWeeks(currentDayOfWeek, day)
        }
    }

    private fun differenceBetweenDatOfWeeks(firstDayOfWeek: DayOfWeeks, sendDayOfWeeks: DayOfWeeks): Long =
        when {
            firstDayOfWeek == DayOfWeeks.MONDAY && sendDayOfWeeks == DayOfWeeks.MONDAY -> 0
            firstDayOfWeek == DayOfWeeks.MONDAY && sendDayOfWeeks == DayOfWeeks.TUESDAY -> 1
            firstDayOfWeek == DayOfWeeks.MONDAY && sendDayOfWeeks == DayOfWeeks.WEDNESDAY -> 2
            firstDayOfWeek == DayOfWeeks.MONDAY && sendDayOfWeeks == DayOfWeeks.THURSDAY -> 3
            firstDayOfWeek == DayOfWeeks.MONDAY && sendDayOfWeeks == DayOfWeeks.FRIDAY -> 4
            firstDayOfWeek == DayOfWeeks.MONDAY && sendDayOfWeeks == DayOfWeeks.SATURDAY -> 5
            firstDayOfWeek == DayOfWeeks.MONDAY && sendDayOfWeeks == DayOfWeeks.SUNDAY -> 6

            firstDayOfWeek == DayOfWeeks.TUESDAY && sendDayOfWeeks == DayOfWeeks.TUESDAY -> 0
            firstDayOfWeek == DayOfWeeks.TUESDAY && sendDayOfWeeks == DayOfWeeks.WEDNESDAY -> 1
            firstDayOfWeek == DayOfWeeks.TUESDAY && sendDayOfWeeks == DayOfWeeks.THURSDAY -> 2
            firstDayOfWeek == DayOfWeeks.TUESDAY && sendDayOfWeeks == DayOfWeeks.FRIDAY -> 3
            firstDayOfWeek == DayOfWeeks.TUESDAY && sendDayOfWeeks == DayOfWeeks.SATURDAY -> 4
            firstDayOfWeek == DayOfWeeks.TUESDAY && sendDayOfWeeks == DayOfWeeks.SUNDAY -> 5
            firstDayOfWeek == DayOfWeeks.TUESDAY && sendDayOfWeeks == DayOfWeeks.MONDAY -> 6

            firstDayOfWeek == DayOfWeeks.WEDNESDAY && sendDayOfWeeks == DayOfWeeks.WEDNESDAY -> 0
            firstDayOfWeek == DayOfWeeks.WEDNESDAY && sendDayOfWeeks == DayOfWeeks.THURSDAY -> 1
            firstDayOfWeek == DayOfWeeks.WEDNESDAY && sendDayOfWeeks == DayOfWeeks.FRIDAY -> 2
            firstDayOfWeek == DayOfWeeks.WEDNESDAY && sendDayOfWeeks == DayOfWeeks.SATURDAY -> 3
            firstDayOfWeek == DayOfWeeks.WEDNESDAY && sendDayOfWeeks == DayOfWeeks.SUNDAY -> 4
            firstDayOfWeek == DayOfWeeks.WEDNESDAY && sendDayOfWeeks == DayOfWeeks.MONDAY -> 5
            firstDayOfWeek == DayOfWeeks.WEDNESDAY && sendDayOfWeeks == DayOfWeeks.TUESDAY -> 6

            firstDayOfWeek == DayOfWeeks.THURSDAY && sendDayOfWeeks == DayOfWeeks.THURSDAY -> 0
            firstDayOfWeek == DayOfWeeks.THURSDAY && sendDayOfWeeks == DayOfWeeks.FRIDAY -> 1
            firstDayOfWeek == DayOfWeeks.THURSDAY && sendDayOfWeeks == DayOfWeeks.SATURDAY -> 2
            firstDayOfWeek == DayOfWeeks.THURSDAY && sendDayOfWeeks == DayOfWeeks.SUNDAY -> 3
            firstDayOfWeek == DayOfWeeks.THURSDAY && sendDayOfWeeks == DayOfWeeks.MONDAY -> 4
            firstDayOfWeek == DayOfWeeks.THURSDAY && sendDayOfWeeks == DayOfWeeks.TUESDAY -> 5
            firstDayOfWeek == DayOfWeeks.THURSDAY && sendDayOfWeeks == DayOfWeeks.WEDNESDAY -> 6

            firstDayOfWeek == DayOfWeeks.FRIDAY && sendDayOfWeeks == DayOfWeeks.FRIDAY -> 0
            firstDayOfWeek == DayOfWeeks.FRIDAY && sendDayOfWeeks == DayOfWeeks.SATURDAY -> 1
            firstDayOfWeek == DayOfWeeks.FRIDAY && sendDayOfWeeks == DayOfWeeks.SUNDAY -> 2
            firstDayOfWeek == DayOfWeeks.FRIDAY && sendDayOfWeeks == DayOfWeeks.MONDAY -> 3
            firstDayOfWeek == DayOfWeeks.FRIDAY && sendDayOfWeeks == DayOfWeeks.TUESDAY -> 4
            firstDayOfWeek == DayOfWeeks.FRIDAY && sendDayOfWeeks == DayOfWeeks.WEDNESDAY -> 5
            firstDayOfWeek == DayOfWeeks.FRIDAY && sendDayOfWeeks == DayOfWeeks.THURSDAY -> 6

            firstDayOfWeek == DayOfWeeks.SATURDAY && sendDayOfWeeks == DayOfWeeks.SATURDAY -> 0
            firstDayOfWeek == DayOfWeeks.SATURDAY && sendDayOfWeeks == DayOfWeeks.SUNDAY -> 1
            firstDayOfWeek == DayOfWeeks.SATURDAY && sendDayOfWeeks == DayOfWeeks.MONDAY -> 2
            firstDayOfWeek == DayOfWeeks.SATURDAY && sendDayOfWeeks == DayOfWeeks.TUESDAY -> 3
            firstDayOfWeek == DayOfWeeks.SATURDAY && sendDayOfWeeks == DayOfWeeks.WEDNESDAY -> 4
            firstDayOfWeek == DayOfWeeks.SATURDAY && sendDayOfWeeks == DayOfWeeks.THURSDAY -> 5
            firstDayOfWeek == DayOfWeeks.SATURDAY && sendDayOfWeeks == DayOfWeeks.FRIDAY -> 6

            firstDayOfWeek == DayOfWeeks.SUNDAY && sendDayOfWeeks == DayOfWeeks.SUNDAY -> 0
            firstDayOfWeek == DayOfWeeks.SUNDAY && sendDayOfWeeks == DayOfWeeks.MONDAY -> 1
            firstDayOfWeek == DayOfWeeks.SUNDAY && sendDayOfWeeks == DayOfWeeks.TUESDAY -> 2
            firstDayOfWeek == DayOfWeeks.SUNDAY && sendDayOfWeeks == DayOfWeeks.WEDNESDAY -> 3
            firstDayOfWeek == DayOfWeeks.SUNDAY && sendDayOfWeeks == DayOfWeeks.THURSDAY -> 4
            firstDayOfWeek == DayOfWeeks.SUNDAY && sendDayOfWeeks == DayOfWeeks.FRIDAY -> 5
            firstDayOfWeek == DayOfWeeks.SUNDAY && sendDayOfWeeks == DayOfWeeks.SATURDAY -> 6

            else -> throw Exception("Days of weeks index of bound")
        }

}