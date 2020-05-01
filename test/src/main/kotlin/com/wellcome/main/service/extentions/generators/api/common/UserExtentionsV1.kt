package com.wellcome.main.service.extentions.generators.api.common

import com.wellcome.main.dto.api.newDto.common.v1.UserDtoV1
import com.wellcome.main.entity.user.User

fun User.generateUserDtoV1(): UserDtoV1 =
    UserDtoV1(
        id = requireNotNull(this.id),
        name = this.name,
        gender = this.gender.name,
        avatarUrl = this.photoUrl,
        pushAvailable = this.pushAvailable,
        birthday = this.dateOfBirth?.toString() ?: ""
    )