package com.wellcome.main.util.functions

import com.wellcome.main.configuration.security.model.ProfileContext
import com.wellcome.main.configuration.security.model.RawAccessJwtToken
import com.wellcome.main.configuration.security.model.UserContext
import com.wellcome.main.configuration.utils.ApplicationContextProvider
import com.wellcome.main.entity.institution.DayOfWeeks
import com.wellcome.main.entity.institution.Institution
import com.wellcome.main.entity.institution.InstitutionCategoryType
import com.wellcome.main.service.management.api.UserManagementService
import com.wellcome.main.util.enumerators.SearchInstitutionDays
import org.apache.commons.lang3.StringUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.time.*

fun getHeader(key: String): String? =
    (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes)
        .request.getHeader(key)

fun getQueryString(key: String): String? =
    (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes)
        .request.getParameter(key)

fun getUserId(): Long? {
    return SecurityContextHolder.getContext().authentication.principal?.let {
        (it as UserContext).id
    }
}

fun getProfileContext(): ProfileContext? =
    SecurityContextHolder.getContext().authentication.principal?.let {
        it as ProfileContext
    }

fun getInstitutionCategoryByTime(userZonedDateTime: ZonedDateTime): InstitutionCategoryType {
    val timeZoneId = userZonedDateTime.zone.toString()
    val currentDateTime = userZonedDateTime.toLocalDateTime()
    return when {
        currentDateTime > "06:00".convertToLocalDateTime(timeZoneId) &&
            currentDateTime < "12:00".convertToLocalDateTime(timeZoneId) -> InstitutionCategoryType.LOUNGE_BAR
        currentDateTime > "12:00".convertToLocalDateTime(timeZoneId) &&
            currentDateTime < "18:00".convertToLocalDateTime(timeZoneId) -> InstitutionCategoryType.HOOKAH
        currentDateTime > "18:00".convertToLocalDateTime(timeZoneId) &&
            currentDateTime < "23:00".convertToLocalDateTime(timeZoneId) -> InstitutionCategoryType.BAR
        else -> InstitutionCategoryType.NIGHT_CLUB
    }
}

fun getUserIdWithRequestContext(token: String?): Long? {
    val userManagementService =
        ApplicationContextProvider.getApplicationContext().getBean(UserManagementService::class.java) as UserManagementService
    return try {
        if (token != null && token.isNotEmpty()) {
            token.let(userManagementService::getOrCreateByToken).id
        } else null
    } catch (e: Exception) {
        null
    }
}

fun getLocalityId(): Long? {
    return SecurityContextHolder.getContext().authentication.principal?.let {
        (it as UserContext).localityId
    }
}

fun getToken(): RawAccessJwtToken? {
    return SecurityContextHolder.getContext().authentication.credentials?.let {
        it as RawAccessJwtToken
    }
}

fun generateLocalityTopic(localityName: String): String {
    var topic: String = localityName
    arrayOf(" ", "-", ".", ",", ":").forEach {
        topic = topic.replace(it, "")
    }
    return topic
}

fun getCurrentWeek(timezoneId: String): DayOfWeeks =
    ZonedDateTime.now(ZoneId.of(timezoneId)).dayOfWeek.toDayOfWeek()

fun String.convertToLocalDateTimeOrNull(timeZoneId: String): LocalDateTime? = try {
    this.convertToLocalDateTime(timeZoneId)
} catch (e: Exception) {
    null
}

inline fun tryOrFalse(func: () -> Boolean): Boolean = try {
    func()
} catch (e: Exception) {
    false
}

fun String.convertToLocalDateTime(timeZoneId: String): LocalDateTime {
    val zoneId = ZoneId.of(timeZoneId)
    val ldt = LocalDate.now(zoneId)
    return ldt.atTime(StringUtils.left(this, 2).toInt(), StringUtils.right(this, 2).toInt())
}

fun getDay(day: String, userZonedDateTime: ZonedDateTime): DayOfWeeks {
    return try {
        when (SearchInstitutionDays.valueOf(day)) {
            SearchInstitutionDays.NOW -> userZonedDateTime.dayOfWeek.toDayOfWeek()
            SearchInstitutionDays.TODAY -> userZonedDateTime.dayOfWeek.toDayOfWeek()
            SearchInstitutionDays.TOMORROW -> userZonedDateTime.plusDays(1).dayOfWeek.toDayOfWeek()
            SearchInstitutionDays.BIRTHDAY -> userZonedDateTime.dayOfWeek.toDayOfWeek()
        }
    } catch (e: IllegalArgumentException) {
        DayOfWeeks.valueOf(day)
    }
}

fun encryptPassword(password: String): String = BCrypt.hashpw(password, BCrypt.gensalt())

fun distanceBetweenLatLon(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val radLat1 = Math.toRadians(lat1)
    val radLon1 = Math.toRadians(lon1)
    val radLat2 = Math.toRadians(lat2)
    val radLon2 = Math.toRadians(lon2)
    val earthRadius = 6371.01
    return earthRadius * Math.acos(Math.sin(radLat1) * Math.sin(radLat2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.cos(radLon1 - radLon2))
}

object MathSupport {
    fun divisionFive(value: Double): Double = value / 5
    fun multiplicationSixty(value: Double): Double = value * 60
    fun divisionHundred(value: Double): Double = value / 100
    fun multiplicationTwenty(value: Double): Double = value * 20
}


data class InstitutionSort(val institutionId: Long,
                           val sortAttribute: Double)