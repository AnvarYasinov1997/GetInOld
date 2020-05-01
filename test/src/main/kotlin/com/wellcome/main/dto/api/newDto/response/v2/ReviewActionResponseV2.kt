package com.wellcome.main.dto.api.newDto.response.v2

import com.wellcome.main.dto.api.newDto.common.v1.InstitutionDtoV1
import com.wellcome.main.dto.api.newDto.common.v2.ReviewDtoV2

data class ReviewActionResponseV2(val reviewDto: ReviewDtoV2,
                                  val institutionDto: InstitutionDtoV1)