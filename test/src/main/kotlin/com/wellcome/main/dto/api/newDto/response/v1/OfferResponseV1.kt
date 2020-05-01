package com.wellcome.main.dto.api.newDto.response.v1

import com.wellcome.main.dto.api.newDto.common.v1.BlockOffersDtoV1
import com.wellcome.main.dto.api.newDto.common.v1.OfferDtoV1

data class OfferResponseV1(
    val pageTitle: String,
    val blockOffers: List<BlockOffersDtoV1>,
    val workDayAttributes: List<OfferWorkDayAttributeDtoV1>
)

data class OfferWorkDayAttributeDtoV1(val title: String,
                                      val big: Boolean,
                                      val day: String)