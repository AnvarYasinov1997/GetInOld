package com.wellcome.main.dto.api.newDto.common.v1

import com.wellcome.main.dto.api.newDto.common.v2.OfferDtoV2

data class SelectionDtoV1(val id: Long,
                          val name: String,
                          val offerDtoList: List<OfferDtoV2>)