package com.wellcome.main.dto.api.newDto.response.v3

import com.wellcome.main.dto.api.newDto.common.v1.EventDtoV1
import com.wellcome.main.dto.api.newDto.common.v1.InstitutionDtoV1
import com.wellcome.main.dto.api.newDto.common.v1.UserDtoV1
import com.wellcome.main.dto.api.newDto.common.v2.ReviewDtoV2

data class UserProfileResponseV3(val userDto: UserDtoV1,
                                 val savedInstitutions: List<InstitutionDtoV1>,
                                 val savedEvents: List<EventDtoV1>,
                                 val reviews: List<ReviewDtoV2>)