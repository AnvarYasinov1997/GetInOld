package com.wellcome.main.service.management.api

import com.wellcome.main.dto.api.newDto.request.v1.UserStoryRequestV1
import com.wellcome.main.entity.story.UserStory
import com.wellcome.main.service.facade.story.UserStoryService
import com.wellcome.main.service.facade.user.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityNotFoundException

interface UserStoryManagementService {
    fun add(googleUid: String, request: UserStoryRequestV1)
}

@Service
open class DefaultUserStoryManagementService @Autowired constructor(
    private val userService: UserService,
    private val userStoryService: UserStoryService
) : UserStoryManagementService {

    @Transactional
    override fun add(googleUid: String, request: UserStoryRequestV1) {
        val user = userService.findByGoogleUid(googleUid)
            ?: throw EntityNotFoundException("Uset width $googleUid is not found to database")

        UserStory(
            title = request.title,
            text = request.text,
            pictureUrl = request.pictureUrl,
            user = user
        ).let(userStoryService::saveOrUpdate)
    }

}