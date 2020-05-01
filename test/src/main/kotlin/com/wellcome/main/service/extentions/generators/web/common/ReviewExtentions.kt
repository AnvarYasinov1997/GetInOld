package com.wellcome.main.service.extentions.generators.web.common

import com.wellcome.main.dto.web.common.ReviewDto
import com.wellcome.main.entity.institution.InstitutionReview

fun InstitutionReview.generateReviewDto(): ReviewDto =
    ReviewDto(
        reviewId = this.id!!,
        starCount = this.startCount,
        feedback = this.feedback,
        userDto = this.user.generateUserDto()
    )

fun List<InstitutionReview>.generateReviewDtoList(): List<ReviewDto> =
    this.map {
        ReviewDto(
            reviewId = it.id!!,
            starCount = it.startCount,
            feedback = it.feedback,
            userDto = it.user.generateUserDto()
        )
    }