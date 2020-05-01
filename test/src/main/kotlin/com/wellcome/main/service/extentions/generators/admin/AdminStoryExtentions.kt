package com.wellcome.main.service.extentions.generators.admin

import com.wellcome.main.dto.admin.common.StoryDto
import com.wellcome.main.entity.story.Story

fun List<Story>.generateStoryDtoList(): List<StoryDto> =
    this.map {
        StoryDto(
            id = requireNotNull(it.id),
            title = it.title,
            text = it.text,
            type = it.type.name,
            pictureUrl = it.pictureUrl,
            reviewForbidden = it.reviewForbiddenContent
        )
    }