package com.wellcome.main.dto.api.newDto.response.v1

import com.wellcome.main.dto.api.newDto.common.v1.ReviewDtoV1

data class ReviewResponseV1(val title: String,
                            val reviews: List<ReviewDtoV1>)