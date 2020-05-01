package com.wellcome.main.service.extentions.generators.api.v1

import com.wellcome.main.dto.api.newDto.common.v1.BlockEventDtoV1
import com.wellcome.main.dto.api.newDto.common.v1.BlockInstitutionsDtoV1
import com.wellcome.main.dto.api.newDto.common.v1.BlockReviewDtoV1
import com.wellcome.main.entity.institution.InstitutionReview
import com.wellcome.main.entity.user.User
import com.wellcome.main.dto.api.newDto.response.v1.UserProfileResponseV1
import com.wellcome.main.entity.institution.DayOfWeeks
import com.wellcome.main.entity.institution.Institution
import com.wellcome.main.service.extentions.generators.api.common.*
import com.wellcome.main.wrapper.EntityWrapper
import java.time.ZonedDateTime

fun User.generateUserProfileResponseV1(day: DayOfWeeks, bookmarks: List<EntityWrapper<Institution>>): UserProfileResponseV1 {
    return UserProfileResponseV1(
        id = requireNotNull(this.id),
        userName = this.name,
        avatarUrl = this.photoUrl,
        savedEvents = BlockEventDtoV1(
            title = "Сохраненные афишы",
            events = emptyList()
        ),
        reviews = BlockReviewDtoV1(
            title = "Ваши отзывы",
            reviews = this.reviews.map(InstitutionReview::generateReviewDtoV1),
            showAll = false // TODO()
        ),
        savedInstitutions = BlockInstitutionsDtoV1(
            title = "Сохраненные заведения",
            institutions = bookmarks.generateInstitutionDtoV1List(day)
        )
    )
}