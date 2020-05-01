package com.wellcome.main.dto.api.newDto.common.v1

data class UserDtoV1(val id: Long,
                     val name: String,
                     val avatarUrl: String,
                     val gender: String,
                     val birthday: String,
                     val pushAvailable: Boolean)
