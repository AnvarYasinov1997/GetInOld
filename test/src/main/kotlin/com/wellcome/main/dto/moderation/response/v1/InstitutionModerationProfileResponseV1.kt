package com.wellcome.main.dto.moderation.response.v1

import com.wellcome.main.dto.api.newDto.common.v1.EventDtoV1
import com.wellcome.main.dto.api.newDto.common.v1.InstitutionDtoV1
import com.wellcome.main.dto.api.newDto.common.v1.OfferDtoV1
import com.wellcome.main.dto.moderation.common.v1.AnalyticConversionDtoV1
import com.wellcome.main.dto.moderation.common.v1.AnalyticEventDtoV1
import java.time.Month

data class InstitutionModerationProfileResponseV1(val institutionDto: InstitutionDtoV1,
                                                  val allTimeConversions: List<AnalyticConversionDtoV1>,
                                                  val month: Month,
                                                  val monthConversions: List<AnalyticConversionDtoV1>,
                                                  val monthEvents: List<AnalyticEventDtoV1>,
                                                  val actualOffers: List<OfferDtoV1>,
                                                  val actualEvents: List<EventDtoV1>)