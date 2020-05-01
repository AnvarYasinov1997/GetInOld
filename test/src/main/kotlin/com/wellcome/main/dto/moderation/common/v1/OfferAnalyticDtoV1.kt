package com.wellcome.main.dto.moderation.common.v1

import com.wellcome.main.dto.api.newDto.common.v2.OfferDtoV2

data class OfferAnalyticDtoV1(val offerDto: OfferDtoV2,
                              val createDate: String?,
                              val expireDate: String?)