package com.wellcome.main.dto.moderation.common.v1

data class AnalyticDtoV1(val userSeenDtoV1: UserSeenDtoV1,
                         val interestingDtoV1: InterestingDtoV1,
                         val conversionDtoV1: ConversionDtoV1)

data class UserSeenDtoV1(val dailyInstitutionSeenDtoV1List: List<DailyInstitutionSeenDtoV1>,
                         val dailyOfferSeenDtoV1List: List<DailyOfferSeenDtoV1>,
                         val dailyEventSeenDtoV1List: List<DailyEventSeenDtoV1>,
                         val dailyBirthdayCampaignDtoList: List<DailyBirthdayCampaignSeenDtoV1>)

data class InterestingDtoV1(val dailyInstitutionProfileClickDtoV1List: List<DailyInstitutionProfileClickDtoV1>,
                            val dailyExpandOfferDtoV1List: List<DailyExpandOfferDtoV1>,
                            val dailyExpandEventDtoV1List: List<DailyExpandEventDtoV1>)

data class ConversionDtoV1(val dailyShowMapDtoV1List: List<DailyShowMapDtoV1>,
                           val dailyCallDtoV1List: List<DailyCallDtoV1>,
                           val dailyTaxiDtoV1List: List<DailyTaxiDtoV1>,
                           val dailyBuildRouteDtoV1List: List<DailyBuildRouteDtoV1>,
                           val dailyBirthdayCampaignUseDtoV1List: List<DailyBirthdayCampaignUseDtoV1>)

//conversion dto child----------------------------
data class DailyShowMapDtoV1(val date: String,
                             val timeShowMapDtoV1List: List<TimeShowMapDtoV1>)

data class TimeShowMapDtoV1(val time: String,
                            val institutionId: Long,
                            val offerId: Long?,
                            val eventId: Long?,
                            val birthdayCampaignId: Long?)

data class DailyCallDtoV1(val date: String,
                          val timeCallDtoV1List: List<TimeCallDtoV1>)

data class TimeCallDtoV1(val time: String,
                         val institutionId: Long,
                         val offerId: Long?,
                         val eventId: Long?,
                         val birthdayCampaignId: Long?)

data class DailyTaxiDtoV1(val date: String,
                          val timeTaxiDtoV1List: List<TimeTaxiDtoV1>)

data class TimeTaxiDtoV1(val time: String,
                         val institutionId: Long,
                         val offerId: Long?,
                         val eventId: Long?,
                         val birthdayCampaignId: Long?)

data class DailyBuildRouteDtoV1(val date: String,
                                val timeBuildRouteDtoV1List: List<TimeBuildRouteDtoV1>)

data class TimeBuildRouteDtoV1(val time: String,
                               val institutionId: Long,
                               val offerId: Long?,
                               val eventId: Long?,
                               val birthdayCampaignId: Long?)

data class DailyBirthdayCampaignUseDtoV1(val date: String,
                                         val timeBirthdayCampaignUseDtoList: List<TimeBirthdayCampaignUseDtoV1>)

data class TimeBirthdayCampaignUseDtoV1(val time: String,
                                        val institutionId: Long,
                                        val campaignId: Long)
//conversion dto child----------------------------

//interesting dto child----------------------------
data class DailyInstitutionProfileClickDtoV1(val date: String,
                                             val timeInstitutionProfileClickDtoV1List: List<TimeInstitutionProfileClickDtoV1>)

data class TimeInstitutionProfileClickDtoV1(val time: String,
                                            val institutionId: Long,
                                            val offerId: Long?,
                                            val eventId: Long?,
                                            val birthdayCampaignId: Long?)

data class DailyExpandOfferDtoV1(val date: String,
                                 val timeExpandOfferDtoV1List: List<TimeExpandOfferDtoV1>)

data class TimeExpandOfferDtoV1(val time: String,
                                val institutionId: Long,
                                val offerId: Long)

data class DailyExpandEventDtoV1(val date: String,
                                 val timeExpandEventDtoV1List: List<TimeExpandEventDtoV1>)

data class TimeExpandEventDtoV1(val time: String,
                                val institutionId: Long,
                                val eventId: Long)
//interesting dto child----------------------------

//user seen dto child----------------------------
data class DailyInstitutionSeenDtoV1(val date: String,
                                     val timeInstitutionSeenDtoV1List: List<TimeInstitutionSeenDtoV1>)

data class TimeInstitutionSeenDtoV1(val time: String,
                                    val institutionId: Long)

data class DailyOfferSeenDtoV1(val date: String,
                               val timeOfferSeenDtoV1List: List<TimeOfferSeenDtoV1>)

data class TimeOfferSeenDtoV1(val time: String,
                              val institutionId: Long,
                              val offerId: Long)

data class DailyEventSeenDtoV1(val date: String,
                               val timeEventSeenDtoV1List: List<TimeEventSeenDtoV1>)

data class TimeEventSeenDtoV1(val time: String,
                              val institutionId: Long,
                              val eventId: Long)

data class DailyBirthdayCampaignSeenDtoV1(val date: String,
                                          val timeBirthdayCampaignSeenDtoV1List: List<TimeBirthdayCampaignSeenDtoV1>)

data class TimeBirthdayCampaignSeenDtoV1(val time: String,
                                         val institutionId: Long,
                                         val campaignId: Long)
//user seen dto child----------------------------