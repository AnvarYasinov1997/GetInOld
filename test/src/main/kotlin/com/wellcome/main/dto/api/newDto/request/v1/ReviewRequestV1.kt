package com.wellcome.main.dto.api.newDto.request.v1

data class ReviewRequestV1(val institutionId: Long,
                           val starCount: Int,
                           val feedback: String)