package com.wellcome.main.dto.web.common

data class StoryDto(val id: Long,
                    val title: String,
                    val content: String,
                    val pictureUrl: String,
                    val liked: Boolean,
                    val likeCount: Long)