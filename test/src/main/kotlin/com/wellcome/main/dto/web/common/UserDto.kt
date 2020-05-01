package com.wellcome.main.dto.web.common

data class UserDto(val id: Long,
                   val name: String,
                   val avatarUrl: String,
                   val gender: String,
                   val birthday: String,
                   val pushAvailable: Boolean)