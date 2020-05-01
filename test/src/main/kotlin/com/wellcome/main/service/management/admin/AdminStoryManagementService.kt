package com.wellcome.main.service.management.admin

import com.wellcome.main.dto.admin.request.AddStoryRequest
import com.wellcome.main.dto.admin.response.AllStoryResponse
import com.wellcome.main.dto.admin.response.StoryTypeResponse
import com.wellcome.main.entity.story.Story
import com.wellcome.main.entity.story.StoryType
import com.wellcome.main.service.extentions.generators.admin.generateStoryDtoList
import com.wellcome.main.service.facade.story.StoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface AdminStoryManagementService {
    fun getTypes(): StoryTypeResponse
    fun getAll(storyType: String): AllStoryResponse
    fun add(request: AddStoryRequest)
}

@Service
open class DefaultAdminStoryManagementService @Autowired constructor(
    private val storyService: StoryService
) : AdminStoryManagementService {

    @Transactional
    override fun getAll(storyType: String): AllStoryResponse {
        return storyService.findAll().filter {
            it.type == StoryType.valueOf(storyType)
        }.generateStoryDtoList().let(::AllStoryResponse)
    }

    @Transactional
    override fun add(request: AddStoryRequest) {
        Story(
            title = request.storyDto.title,
            text = request.storyDto.text,
            type = StoryType.valueOf(request.storyDto.type),
            reviewForbiddenContent = request.storyDto.reviewForbidden,
            pictureUrl = request.storyDto.pictureUrl
        ).let(storyService::saveOrUpdate)
    }

    override fun getTypes(): StoryTypeResponse {
        return StoryTypeResponse(StoryType.values().map(StoryType::name))
    }

}