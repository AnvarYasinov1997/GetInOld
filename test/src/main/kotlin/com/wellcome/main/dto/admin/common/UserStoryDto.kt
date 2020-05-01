package com.wellcome.main.dto.admin.common

data class UserStoryDto(val id: Long,
                        val title: String,
                        val text: String,
                        val pictureUrl: String,
                        val approved: Boolean)