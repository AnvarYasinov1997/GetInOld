package com.wellcome.main.dto.api.newDto.common.v1

data class StoryDtoV1(val id: Long,
                      val title: String,
                      val content: String,
                      val pictureUrl: String,
                      val liked: Boolean,
                      val likeCount: Long)