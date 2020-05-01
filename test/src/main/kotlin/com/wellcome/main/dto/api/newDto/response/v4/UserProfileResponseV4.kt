package com.wellcome.main.dto.api.newDto.response.v4

import com.wellcome.main.dto.api.newDto.common.v1.BirthdayCampaignsDtoV1
import com.wellcome.main.dto.api.newDto.common.v1.EventDtoV1
import com.wellcome.main.dto.api.newDto.common.v1.InstitutionDtoV1
import com.wellcome.main.dto.api.newDto.common.v1.UserDtoV1
import com.wellcome.main.dto.api.newDto.common.v2.ReviewDtoV2

data class UserProfileResponseV4(val userDto: UserDtoV1,
                                 val savedInstitutions: List<InstitutionDtoV1>,
                                 val savedEvents: List<EventDtoV1>,
                                 val reviews: List<ReviewDtoV2>,
                                 val birthdayCampaignsDto: BirthdayCampaignsDtoV1?)