package com.wellcome.main.dto.web.common

data class ReviewDto(val reviewId: Long,
                     val starCount: Long,
                     val feedback: String,
                     val userDto: UserDto)