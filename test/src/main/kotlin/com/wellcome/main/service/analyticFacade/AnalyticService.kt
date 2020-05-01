package com.wellcome.main.service.analyticFacade

import com.wellcome.main.model.CommonAnalyticModel
import com.wellcome.main.repository.remote.bigQuery.BigQueryRepository
import com.wellcome.main.util.variables.MobileAnalyticEvent
import com.wellcome.main.util.variables.ParamKey
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

interface AnalyticService {
    fun findSharedCount(institutionId: Long, timeLowRange: String, timeTopRange: String): Long
    fun findCommonAnalytics(institutionId: Long, timeLowRange: String, timeTopRange: String, isAllTime: Boolean = false): List<CommonAnalyticModel>
}

@Service
open class DefaultAnalyticService @Autowired constructor(
    private val bigQueryRepository: BigQueryRepository
) : AnalyticService {

    override fun findSharedCount(institutionId: Long, timeLowRange: String, timeTopRange: String): Long {
        val query = generateQuery(institutionId, MobileAnalyticEvent.SHARE.value,
            timeLowRange = timeLowRange, timeTopRange = timeTopRange, isOnlyInstitutionId = true)
        return bigQueryRepository.getData(query).totalRows
    }

    override fun findCommonAnalytics(institutionId: Long, timeLowRange: String, timeTopRange: String, isAllTime: Boolean): List<CommonAnalyticModel> {
        val query = generateQuery(institutionId, "",
            timeLowRange = timeLowRange, timeTopRange = timeTopRange, isOffer = true, isEvent = true, isBirthdayCampaign = true, isCommon = true, isAllTime = isAllTime)
        return bigQueryRepository.getData(query).iterateAll().map {
            CommonAnalyticModel(
                institutionId = it[ParamKey.INSTITUTION_ID].longValue,
                offerId = it.get(ParamKey.OFFER_ID).let { field -> if (!field.isNull) field.longValue else null },
                eventId = it[ParamKey.EVENT_ID].let { field -> if (!field.isNull) field.longValue else null },
                timestamp = it[ParamKey.EVENT_TIMESTAMP].longValue,
                date = it[ParamKey.EVENT_DATE].stringValue,
                name = it[ParamKey.EVENT_NAME].stringValue,
                birthdayCampaignId = it.get(ParamKey.BIRTHDAY_CAMPAIGN_ID).let { field -> if (!field.isNull) field.longValue else null }
            )
        }
    }

    private fun generateQuery(institutionId: Long, event: String,
                              timeLowRange: String, timeTopRange: String,
                              isOffer: Boolean = false, isEvent: Boolean = false, isBirthdayCampaign: Boolean = false,
                              isCommon: Boolean = false, isAllTime: Boolean = false, isOnlyInstitutionId: Boolean = false): String {
        val offerColumn = if (isOffer) "(select value.int_value from UNNEST(event_params) where key=\"offer_id\") as offer_id," else ""
        val eventColumn = if (isEvent) "(select value.int_value from UNNEST(event_params) where key=\"event_id\") as event_id," else ""
        val birthdayCampaignColumn = if(isBirthdayCampaign) "(select value.int_value from UNNEST(event_params) where key=\"birthday_campaign_id\") as birthday_campaign_id," else ""
        val events = if (!isCommon) "event_name = \"$event\"" else StringBuilder().let { builder ->
            MobileAnalyticEvent.values().forEach {
                builder.append("event_name = \"${it.value}\"")
                builder.append(" OR ")
            }

            return@let builder.toString().dropLast(3)
        }
        val eventKeys = if (!isOnlyInstitutionId) "event_name, event_date, event_timestamp" else ""
        val timeRange = if (isAllTime) "" else "AND (_TABLE_SUFFIX between '$timeLowRange' and '$timeTopRange')"
        return "SELECT x.value.int_value as institution_id, $offerColumn $eventColumn $birthdayCampaignColumn $eventKeys\n" +
            "FROM `get-in-e617c.analytics_198378995.events_*`,\n" +
            "UNNEST(event_params) as x\n" +
            "WHERE  x.value.int_value = $institutionId AND ($events)\n" +
            "AND x.key = \"institution_id\"\n" + timeRange
    }

}