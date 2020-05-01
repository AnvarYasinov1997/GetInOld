package com.wellcome.main.dto.api.newDto.common.v1

data class ReviewDtoV1(val userId: Long,
                       val userName: String,
                       val userAvatarUrl: String,
                       val feedback: String,
                       val starCount: Int)

data class BlockReviewDtoV1(val title: String,
                            val reviews: List<ReviewDtoV1>,
                            val showAll: Boolean)