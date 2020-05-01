package com.wellcome.main.service.extentions.generators.api.common

import com.wellcome.main.dto.moderation.common.v1.*
import com.wellcome.main.model.CommonAnalyticModel
import java.time.LocalDate
import java.time.LocalTime

fun MutableMap<LocalDate, List<Pair<LocalTime, CommonAnalyticModel>>>.generateDailyInstitutionSeenDtoV1List(): List<DailyInstitutionSeenDtoV1> {
    return this.map {
        return@map DailyInstitutionSeenDtoV1(
            date = it.key.toString(),
            timeInstitutionSeenDtoV1List = it.value.generateTimeInstitutionSeenDtoV1List()
        )
    }
}

fun List<Pair<LocalTime, CommonAnalyticModel>>.generateTimeInstitutionSeenDtoV1List(): List<TimeInstitutionSeenDtoV1> {
    return this.map {
        return@map TimeInstitutionSeenDtoV1(
            time = it.first.toString(),
            institutionId = it.second.institutionId
        )
    }
}

fun MutableMap<LocalDate, List<Pair<LocalTime, CommonAnalyticModel>>>.generateDailyOfferDtoV1List(): List<DailyOfferSeenDtoV1> {
    return this.map {
        return@map DailyOfferSeenDtoV1(
            date = it.key.toString(),
            timeOfferSeenDtoV1List = it.value.generateTimeOfferSeenDtoV1List()
        )
    }
}

fun List<Pair<LocalTime, CommonAnalyticModel>>.generateTimeOfferSeenDtoV1List(): List<TimeOfferSeenDtoV1> {
    return this.map {
        return@map TimeOfferSeenDtoV1(
            time = it.first.toString(),
            offerId = requireNotNull(it.second.offerId),
            institutionId = it.second.institutionId
        )
    }
}

fun MutableMap<LocalDate, List<Pair<LocalTime, CommonAnalyticModel>>>.generateDailyEventSeenDtoV1List(): List<DailyEventSeenDtoV1> {
    return this.map {
        return@map DailyEventSeenDtoV1(
            date = it.key.toString(),
            timeEventSeenDtoV1List = it.value.generateTimeEventSeenDtoV1List()
        )
    }
}

fun List<Pair<LocalTime, CommonAnalyticModel>>.generateTimeEventSeenDtoV1List(): List<TimeEventSeenDtoV1> {
    return this.map {
        return@map TimeEventSeenDtoV1(
            time = it.first.toString(),
            eventId = requireNotNull(it.second.eventId),
            institutionId = it.second.institutionId
        )
    }
}

fun MutableMap<LocalDate, List<Pair<LocalTime, CommonAnalyticModel>>>.generateDailyBirthdayCampaignSeenDtoV1List(): List<DailyBirthdayCampaignSeenDtoV1> {
    return this.map {
        return@map DailyBirthdayCampaignSeenDtoV1(
            date = it.key.toString(),
            timeBirthdayCampaignSeenDtoV1List = it.value.generateTimeBirthdayCampaignSeenDtoV1List()
        )
    }
}

fun List<Pair<LocalTime, CommonAnalyticModel>>.generateTimeBirthdayCampaignSeenDtoV1List(): List<TimeBirthdayCampaignSeenDtoV1> {
    return this.map {
        return@map TimeBirthdayCampaignSeenDtoV1(
            time = it.first.toString(),
            campaignId = requireNotNull(it.second.birthdayCampaignId),
            institutionId = it.second.institutionId
        )
    }
}

fun MutableMap<LocalDate, List<Pair<LocalTime, CommonAnalyticModel>>>.generateDailyBirthdayCampaignUseDtoV1List(): List<DailyBirthdayCampaignUseDtoV1> {
    return this.map {
        return@map DailyBirthdayCampaignUseDtoV1(
            date = it.key.toString(),
            timeBirthdayCampaignUseDtoList = it.value.generateTimeBirthdayCampaignUseDtoV1List()
        )
    }
}

fun List<Pair<LocalTime, CommonAnalyticModel>>.generateTimeBirthdayCampaignUseDtoV1List(): List<TimeBirthdayCampaignUseDtoV1> {
    return this.map {
        return@map TimeBirthdayCampaignUseDtoV1(
            time = it.first.toString(),
            campaignId = requireNotNull(it.second.birthdayCampaignId),
            institutionId = it.second.institutionId
        )
    }
}

fun MutableMap<LocalDate, List<Pair<LocalTime, CommonAnalyticModel>>>.generateDailyInstitutionProfileClickDtoV1List(): List<DailyInstitutionProfileClickDtoV1> {
    return this.map {
        return@map DailyInstitutionProfileClickDtoV1(
            date = it.key.toString(),
            timeInstitutionProfileClickDtoV1List = it.value.generateTimeInstitutionProfileClickDtoV1List()
        )
    }
}

fun List<Pair<LocalTime, CommonAnalyticModel>>.generateTimeInstitutionProfileClickDtoV1List(): List<TimeInstitutionProfileClickDtoV1> {
    return this.map {
        return@map TimeInstitutionProfileClickDtoV1(
            time = it.first.toString(),
            eventId = it.second.eventId,
            offerId = it.second.offerId,
            birthdayCampaignId = it.second.birthdayCampaignId,
            institutionId = it.second.institutionId
        )
    }
}

fun MutableMap<LocalDate, List<Pair<LocalTime, CommonAnalyticModel>>>.generateDailyExpandOfferDtoV1List(): List<DailyExpandOfferDtoV1> {
    return this.map {
        return@map DailyExpandOfferDtoV1(
            date = it.key.toString(),
            timeExpandOfferDtoV1List = it.value.generateTimeExpandOfferDtoV1List()
        )
    }
}

fun List<Pair<LocalTime, CommonAnalyticModel>>.generateTimeExpandOfferDtoV1List(): List<TimeExpandOfferDtoV1> {
    return this.map {
        return@map TimeExpandOfferDtoV1(
            time = it.first.toString(),
            offerId = requireNotNull(it.second.offerId),
            institutionId = it.second.institutionId
        )
    }
}

fun MutableMap<LocalDate, List<Pair<LocalTime, CommonAnalyticModel>>>.generateDailyExpandEventDtoV1List(): List<DailyExpandEventDtoV1> {
    return this.map {
        return@map DailyExpandEventDtoV1(
            date = it.key.toString(),
            timeExpandEventDtoV1List = it.value.generateTimeExpandEventDtoV1List()
        )
    }
}

fun List<Pair<LocalTime, CommonAnalyticModel>>.generateTimeExpandEventDtoV1List(): List<TimeExpandEventDtoV1> {
    return this.map {
        return@map TimeExpandEventDtoV1(
            time = it.first.toString(),
            eventId = requireNotNull(it.second.eventId),
            institutionId = it.second.institutionId
        )
    }
}

fun MutableMap<LocalDate, List<Pair<LocalTime, CommonAnalyticModel>>>.generateDailyShowMapDtoV1List(): List<DailyShowMapDtoV1> {
    return this.map {
        return@map DailyShowMapDtoV1(
            date = it.key.toString(),
            timeShowMapDtoV1List = it.value.generateTimeShowMapDtoV1List()
        )
    }
}

fun List<Pair<LocalTime, CommonAnalyticModel>>.generateTimeShowMapDtoV1List(): List<TimeShowMapDtoV1> {
    return this.map {
        return@map TimeShowMapDtoV1(
            time = it.first.toString(),
            eventId = it.second.eventId,
            offerId = it.second.offerId,
            birthdayCampaignId = it.second.birthdayCampaignId,
            institutionId = it.second.institutionId
        )
    }
}

fun MutableMap<LocalDate, List<Pair<LocalTime, CommonAnalyticModel>>>.generateDailyCallDtoV1List(): List<DailyCallDtoV1> {
    return this.map {
        return@map DailyCallDtoV1(
            date = it.key.toString(),
            timeCallDtoV1List = it.value.generateTimeCallDtoV1List()
        )
    }
}

fun List<Pair<LocalTime, CommonAnalyticModel>>.generateTimeCallDtoV1List(): List<TimeCallDtoV1> {
    return this.map {
        return@map TimeCallDtoV1(
            time = it.first.toString(),
            eventId = it.second.eventId,
            offerId = it.second.offerId,
            birthdayCampaignId = it.second.birthdayCampaignId,
            institutionId = it.second.institutionId
        )
    }
}

fun MutableMap<LocalDate, List<Pair<LocalTime, CommonAnalyticModel>>>.generateDailyTaxiDtoV1List(): List<DailyTaxiDtoV1> {
    return this.map {
        return@map DailyTaxiDtoV1(
            date = it.key.toString(),
            timeTaxiDtoV1List = it.value.generateTimeTaxiDtoV1List()
        )
    }
}

fun List<Pair<LocalTime, CommonAnalyticModel>>.generateTimeTaxiDtoV1List(): List<TimeTaxiDtoV1> {
    return this.map {
        return@map TimeTaxiDtoV1(
            time = it.first.toString(),
            eventId = it.second.eventId,
            offerId = it.second.offerId,
            birthdayCampaignId = it.second.birthdayCampaignId,
            institutionId = it.second.institutionId
        )
    }
}

fun MutableMap<LocalDate, List<Pair<LocalTime, CommonAnalyticModel>>>.generateDailyBuildRouteDtoV1List(): List<DailyBuildRouteDtoV1> {
    return this.map {
        return@map DailyBuildRouteDtoV1(
            date = it.key.toString(),
            timeBuildRouteDtoV1List = it.value.generateTimeBuildRouteDtoV1List()
        )
    }
}

fun List<Pair<LocalTime, CommonAnalyticModel>>.generateTimeBuildRouteDtoV1List(): List<TimeBuildRouteDtoV1> {
    return this.map {
        return@map TimeBuildRouteDtoV1(
            time = it.first.toString(),
            eventId = it.second.eventId,
            offerId = it.second.offerId,
            birthdayCampaignId = it.second.birthdayCampaignId,
            institutionId = it.second.institutionId
        )
    }
}