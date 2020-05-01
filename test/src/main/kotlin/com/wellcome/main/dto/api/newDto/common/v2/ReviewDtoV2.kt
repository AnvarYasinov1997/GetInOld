package com.wellcome.main.dto.api.newDto.common.v2

import com.wellcome.main.dto.api.newDto.common.v1.UserDtoV1

data class ReviewDtoV2(val reviewId: Long,
                       val starCount: Long,
                       val feedback: String,
                       val userDto: UserDtoV1)