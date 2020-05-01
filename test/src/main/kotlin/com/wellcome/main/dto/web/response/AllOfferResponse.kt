package com.wellcome.main.dto.web.response

import com.wellcome.main.dto.web.common.BlockOffersDto

data class AllOfferResponse(val title: String,
                            val blockOffers: List<BlockOffersDto>)