package com.wellcome.main.dto.api.newDto.response.v2

import com.wellcome.main.dto.api.newDto.common.v2.BlockOffersDtoV2

data class FullOfferResponseV2(val title: String,
                               val blockOffers: List<BlockOffersDtoV2>)