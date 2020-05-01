package com.wellcome.main.service.extentions.generators.api.v2

import com.wellcome.main.dto.api.newDto.common.v1.BlockEventDtoV1
import com.wellcome.main.dto.api.newDto.common.v1.BlockInstitutionsDtoV1
import com.wellcome.main.dto.api.newDto.common.v1.BlockReviewDtoV1
import com.wellcome.main.dto.api.newDto.response.v2.UserProfileResponseV2
import com.wellcome.main.entity.institution.DayOfWeeks
import com.wellcome.main.entity.institution.Institution
import com.wellcome.main.entity.institution.InstitutionReview
import com.wellcome.main.entity.user.User
import com.wellcome.main.service.extentions.generators.api.common.generateInstitutionDtoV1List
import com.wellcome.main.service.extentions.generators.api.common.generateReviewDtoV1
import com.wellcome.main.service.extentions.generators.api.common.generateUserDtoV1
import com.wellcome.main.wrapper.EntityWrapper

fun User.generateUserProfileResponseV2(day: DayOfWeeks, bookmarks: List<EntityWrapper<Institution>>): UserProfileResponseV2 {
    return UserProfileResponseV2(
        userDto = this.generateUserDtoV1(),
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