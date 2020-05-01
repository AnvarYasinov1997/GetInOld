package com.wellcome.main.service.facade.story

import com.wellcome.main.entity.story.Story
import com.wellcome.main.repository.local.postgre.StoryRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

interface StoryService : BaseService<Story>

@Service
open class DefaultStoryService @Autowired constructor(
    private val storyRepository: StoryRepository
) : StoryService,
    DefaultBaseService<Story>(Story::class.java.simpleName, storyRepository)