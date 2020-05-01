package com.wellcome.main.service.extentions.generators.api.common

import com.wellcome.main.dto.api.newDto.common.v1.StoryDtoV1
import com.wellcome.main.entity.story.Story
import com.wellcome.main.entity.story.StoryFeedbackType
import com.wellcome.main.wrapper.Delegate
import com.wellcome.main.wrapper.EntityWrapper

fun List<EntityWrapper<Story>>.generateStoryDtoV1List(): List<StoryDtoV1> =
    this.map {
        var userStoryDelegate: Delegate.UserStoryDelegate? = null
        it.delegates.forEach { delegate ->
            when (delegate) {
                is Delegate.UserStoryDelegate -> userStoryDelegate = delegate
            }
        }
        StoryDtoV1(
            id = it.entity.getIdNotNull(),
            title = it.entity.title,
            content = it.entity.text,
            liked = userStoryDelegate?.liked ?: false,
            pictureUrl = it.entity.pictureUrl,
            likeCount = it.entity.storyFeedback.filter { x -> x.type == StoryFeedbackType.LIKE }.size.toLong()
        )
    }