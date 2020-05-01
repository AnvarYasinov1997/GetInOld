package com.wellcome.main.dto.admin.common

data class StoryDto(val id: Long,
                    val title: String,
                    val text: String,
                    val pictureUrl: String,
                    val type: String,
                    val reviewForbidden: Boolean)