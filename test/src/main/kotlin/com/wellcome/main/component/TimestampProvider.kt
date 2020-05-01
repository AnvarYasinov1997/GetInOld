package com.wellcome.main.component

import com.wellcome.main.configuration.utils.ApplicationContextProvider
import com.wellcome.main.service.facade.LocalityService
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.Month
import java.time.ZoneId
import java.time.ZonedDateTime

interface TimestampProvider {
    fun concatenateDateTimeByArguments(year: String, month: String, day: String): String
    fun getCurrentTimeNano(): Long
    fun getCurrentTimeMillis(): Long
    fun getCurrentMonth(): Month
    fun getCurrentYear(): Long
    fun epochNanosecondsToZonedDateTime(epochTime: Long, timezone: String): ZonedDateTime
    fun getServerZonedDateTime(): ZonedDateTime
    fun getUserZonedDateTime(): ZonedDateTime
    fun getUserZonedDateTimeByTimeZoneId(timeZoneId: String): ZonedDateTime

}

@Component
open class DefaultTimestampProvider : TimestampProvider {

    override fun concatenateDateTimeByArguments(year: String, month: String, day: String): String {
        if(month.toLong() > 12 || month.toLong() < 1) throw Exception("Month range is now valid")
        if(day.toLong() > 31 || day.toLong() < 1) throw Exception("Day range is now valid")
        val formattableDay = if (day.length == 1) "0$day" else day
        return "$year$month$formattableDay"
    }

    override fun getCurrentTimeNano(): Long = System.nanoTime()

    override fun getCurrentTimeMillis(): Long =
        Instant.now().toEpochMilli()

    override fun getCurrentMonth(): Month {
        val localityService =
            ApplicationContextProvider.getApplicationContext().getBean(LocalityService::class.java) as LocalityService
        return ZonedDateTime.now(ZoneId.of(localityService.findById(5).timezone)).month
    }

    override fun epochNanosecondsToZonedDateTime(epochTime: Long, timezone: String): ZonedDateTime {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochTime / 1000), ZoneId.of(timezone))
    }

    override fun getCurrentYear(): Long {
        val localityService =
            ApplicationContextProvider.getApplicationContext().getBean(LocalityService::class.java) as LocalityService
        return ZonedDateTime.now(ZoneId.of(localityService.findById(5).timezone)).year.toLong()
    }

    override fun getServerZonedDateTime(): ZonedDateTime {
        return ZonedDateTime.now()
    }

    override fun getUserZonedDateTime(): ZonedDateTime {
        val localityService =
            ApplicationContextProvider.getApplicationContext().getBean(LocalityService::class.java) as LocalityService
//        return ZonedDateTime.parse("2019-09-19T00:01:22.680+06:00[Asia/Bishkek]")
        return ZonedDateTime.now(ZoneId.of(localityService.findById(5).timezone))
    }

    override fun getUserZonedDateTimeByTimeZoneId(timeZoneId: String): ZonedDateTime {
        return ZonedDateTime.now(ZoneId.of(timeZoneId))
    }
}