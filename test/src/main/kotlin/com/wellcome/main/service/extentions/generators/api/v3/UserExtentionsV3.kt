package com.wellcome.main.service.extentions.generators.api.v3

import com.wellcome.main.dto.api.newDto.response.v3.UserProfileResponseV3
import com.wellcome.main.entity.institution.DayOfWeeks
import com.wellcome.main.entity.institution.Institution
import com.wellcome.main.entity.user.User
import com.wellcome.main.service.extentions.generators.api.common.generateInstitutionDtoV1List
import com.wellcome.main.service.extentions.generators.api.common.generateUserDtoV1
import com.wellcome.main.service.extentions.generators.api.common.v2.generateReviewDtoV2List
import com.wellcome.main.wrapper.EntityWrapper

fun User.generateUserProfileResponseV3(day: DayOfWeeks, bookmarks: List<EntityWrapper<Institution>>): UserProfileResponseV3 {
    return UserProfileResponseV3(
        userDto = this.generateUserDtoV1(),
        savedEvents = emptyList(),
        reviews = this.reviews.generateReviewDtoV2List(),
        savedInstitutions = bookmarks.generateInstitutionDtoV1List(day)
    )
}