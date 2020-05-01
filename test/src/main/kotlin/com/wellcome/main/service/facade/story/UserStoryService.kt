package com.wellcome.main.service.facade.story

import com.wellcome.main.entity.story.UserStory
import com.wellcome.main.repository.local.postgre.UserStoryRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

interface UserStoryService : BaseService<UserStory>

@Service
open class DefaultUserStoryService @Autowired constructor(
    private val userStoryRepository: UserStoryRepository
) : DefaultBaseService<UserStory>(UserStory::class.java.simpleName, userStoryRepository),
    UserStoryService
