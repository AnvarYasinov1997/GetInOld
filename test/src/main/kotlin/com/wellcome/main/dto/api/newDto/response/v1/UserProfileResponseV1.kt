package com.wellcome.main.dto.api.newDto.response.v1

import com.wellcome.main.dto.api.newDto.common.v1.BlockEventDtoV1
import com.wellcome.main.dto.api.newDto.common.v1.BlockInstitutionsDtoV1
import com.wellcome.main.dto.api.newDto.common.v1.BlockReviewDtoV1
import com.wellcome.main.dto.api.newDto.common.v1.UserDtoV1

data class UserProfileResponseV1(val id: Long,
                                 val userName: String,
                                 val avatarUrl: String,
                                 val savedInstitutions: BlockInstitutionsDtoV1,
                                 val savedEvents: BlockEventDtoV1,
                                 val reviews: BlockReviewDtoV1)

data class EditUserResponseV1(val userDto: UserDtoV1)