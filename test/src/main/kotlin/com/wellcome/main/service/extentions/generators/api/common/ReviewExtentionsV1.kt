package com.wellcome.main.service.extentions.generators.api.common

import com.wellcome.main.entity.institution.InstitutionReview
import com.wellcome.main.dto.api.newDto.common.v1.ReviewDtoV1

fun List<InstitutionReview>.generateReviewDtoListV1(): List<ReviewDtoV1> =
    this.map {
        ReviewDtoV1(
            userId = requireNotNull(it.user.id),
            userName = it.user.name,
            userAvatarUrl = it.user.photoUrl,
            feedback = it.feedback,
            starCount = it.startCount.toInt()
        )
    }

fun InstitutionReview.generateReviewDtoV1(): ReviewDtoV1 =
    ReviewDtoV1(
        userId = requireNotNull(this.user.id),
        userName = this.user.name,
        userAvatarUrl = this.user.photoUrl,
        feedback = this.feedback,
        starCount = this.startCount.toInt()
    )