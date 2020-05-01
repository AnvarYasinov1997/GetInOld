package com.wellcome.main.model

data class CommonAnalyticModel(val institutionId: Long,
                               val offerId: Long?,
                               val eventId: Long?,
                               val birthdayCampaignId: Long?,
                               val timestamp: Long,
                               val date: String,
                               val name: String)

data class OfferSeenAnalyticModel(val institutionId: Long,
                                  val offerId: Long,
                                  val timestamp: Long,
                                  val date: String,
                                  val name: String)

data class OfferExpandAnalyticModel(val institutionId: Long,
                                    val offerId: Long,
                                    val timestamp: Long,
                                    val date: String,
                                    val name: String)

data class EventSeenAnalyticModel(val institutionId: Long,
                                  val eventId: Long,
                                  val timestamp: Long,
                                  val date: String,
                                  val name: String)

data class EventExpandAnalyticModel(val institutionId: Long,
                                    val eventId: Long,
                                    val timestamp: Long,
                                    val date: String,
                                    val name: String)

data class InstitutionSeenAnalyticModel(val institutionId: Long,
                                        val timestamp: Long,
                                        val date: String,
                                        val name: String)

data class InstitutionProfileOpenAnalyticModel(val institutionId: Long,
                                               val timestamp: Long,
                                               val date: String,
                                               val name: String)

data class CallATaxiAnalyticModel(val institutionId: Long,
                                  val timestamp: Long,
                                  val date: String,
                                  val name: String)

data class MakeACallAnalyticModel(val institutionId: Long,
                                  val timestamp: Long,
                                  val date: String,
                                  val name: String)

data class ShareAnalyticModel(val institutionId: Long,
                              val timestamp: Long,
                              val date: String,
                              val name: String)

data class ShowMapAnalyticModel(val institutionId: Long,
                                val timestamp: Long,
                                val date: String,
                                val name: String)