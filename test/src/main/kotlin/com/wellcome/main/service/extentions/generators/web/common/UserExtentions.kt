package com.wellcome.main.service.extentions.generators.web.common

import com.wellcome.main.dto.web.common.UserDto
import com.wellcome.main.entity.user.User

fun User.generateUserDto(): UserDto =
    UserDto(
        id = requireNotNull(this.id),
        name = this.name,
        gender = this.gender.name,
        avatarUrl = this.photoUrl,
        pushAvailable = this.pushAvailable,
        birthday = this.dateOfBirth?.toString() ?: ""
    )