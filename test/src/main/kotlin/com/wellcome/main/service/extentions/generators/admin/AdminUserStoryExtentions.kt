package com.wellcome.main.service.extentions.generators.admin

import com.wellcome.main.dto.admin.common.UserStoryDto
import com.wellcome.main.entity.story.UserStory

fun List<UserStory>.generateUserStoryDtoList(): List<UserStoryDto> =
    this.map {
        UserStoryDto(
            id = it.id!!,
            text = it.text,
            title = it.title,
            pictureUrl = it.pictureUrl,
            approved = it.approved
        )
    }