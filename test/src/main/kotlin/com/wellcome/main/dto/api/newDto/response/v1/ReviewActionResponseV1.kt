package com.wellcome.main.dto.api.newDto.response.v1

import com.wellcome.main.dto.api.newDto.common.v1.InstitutionDtoV1
import com.wellcome.main.dto.api.newDto.common.v1.ReviewDtoV1

data class ReviewActionResponseV1(val institutionDto: InstitutionDtoV1,
                                  val reviewDto: ReviewDtoV1)