package com.wellcome.main.service.management.admin

import com.wellcome.main.dto.admin.response.UserStoryResponse
import com.wellcome.main.entity.story.UserStory
import com.wellcome.main.service.extentions.generators.admin.generateUserStoryDtoList
import com.wellcome.main.service.facade.story.UserStoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface AdminUserStoryManagementService {
    fun getAll(): UserStoryResponse
    fun approve(id: Long)
    fun remove(id: Long)
}

@Service
open class DefaultAdminUserStoryManagementService @Autowired constructor(
    private val userStoryService: UserStoryService
) : AdminUserStoryManagementService {

    @Transactional(readOnly = true)
    override fun getAll(): UserStoryResponse {
        val userStories = userStoryService.findAll()
            .filterNot(UserStory::approved)

        return UserStoryResponse(userStories.generateUserStoryDtoList())
    }

    @Transactional
    override fun approve(id: Long) {
        userStoryService.findById(id).apply {
            this.approved = true
        }.let(userStoryService::saveOrUpdate)
    }

    @Transactional
    override fun remove(id: Long) {
        userStoryService.deleteById(id)
    }
}