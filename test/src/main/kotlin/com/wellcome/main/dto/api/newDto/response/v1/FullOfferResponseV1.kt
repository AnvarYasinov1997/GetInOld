package com.wellcome.main.dto.api.newDto.response.v1

import com.wellcome.main.dto.api.newDto.common.v1.BlockOffersDtoV1

data class FullOfferResponseV1(
    val title: String,
    val blockOffers: List<BlockOffersDtoV1>
)