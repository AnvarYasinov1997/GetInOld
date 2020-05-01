package com.wellcome.main.service.extentions.generators.api.v4

import com.wellcome.main.dto.api.newDto.common.v1.BirthdayCampaignsDtoV1
import com.wellcome.main.dto.api.newDto.response.v4.UserProfileResponseV4
import com.wellcome.main.entity.institution.BirthdayCampaignUser
import com.wellcome.main.entity.institution.DayOfWeeks
import com.wellcome.main.entity.institution.Institution
import com.wellcome.main.entity.user.User
import com.wellcome.main.service.extentions.generators.api.common.generateBirthdayCampaignDtoV1List
import com.wellcome.main.service.extentions.generators.api.common.generateInstitutionDtoV1List
import com.wellcome.main.service.extentions.generators.api.common.generateUserDtoV1
import com.wellcome.main.service.extentions.generators.api.common.v2.generateReviewDtoV2List
import com.wellcome.main.wrapper.EntityWrapper

fun User.generateUserProfileResponseV4(day: DayOfWeeks,
                                       birthdayCampaignUser: BirthdayCampaignUser?,
                                       bookmarks: List<EntityWrapper<Institution>>): UserProfileResponseV4 {
    return UserProfileResponseV4(
        userDto = this.generateUserDtoV1(),
        savedEvents = emptyList(),
        reviews = this.reviews.generateReviewDtoV2List(),
        savedInstitutions = bookmarks.generateInstitutionDtoV1List(day),
        birthdayCampaignsDto = birthdayCampaignUser?.let {
            BirthdayCampaignsDtoV1(
                id = it.id!!,
                showBirthdayCampaignsFullScreen = !it.viewed,
                userDto = this.generateUserDtoV1(),
                birthdayCampaignsDtoList = it.birthdayCampaigns.toList().generateBirthdayCampaignDtoV1List()
            )
        }
    )
}