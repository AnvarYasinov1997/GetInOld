package com.wellcome.main.service.extentions.generators.api.common.v2

import com.wellcome.main.dto.api.newDto.common.v2.ReviewDtoV2
import com.wellcome.main.entity.institution.InstitutionReview
import com.wellcome.main.service.extentions.generators.api.common.generateUserDtoV1

fun InstitutionReview.generateReviewDtoV2(): ReviewDtoV2 =
    ReviewDtoV2(
        reviewId = this.id!!,
        starCount = this.startCount,
        feedback = this.feedback,
        userDto = this.user.generateUserDtoV1()
    )

fun List<InstitutionReview>.generateReviewDtoV2List(): List<ReviewDtoV2> =
    this.map {
        ReviewDtoV2(
            reviewId = it.id!!,
            starCount = it.startCount,
            feedback = it.feedback,
            userDto = it.user.generateUserDtoV1()
        )
    }