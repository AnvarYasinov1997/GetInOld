package com.wellcome.main.service.management.api

import com.wellcome.main.dto.api.newDto.response.v1.LikeResponseV1
import com.wellcome.main.dto.api.newDto.response.v1.StoryResponseV1
import com.wellcome.main.entity.story.StoryFeedback
import com.wellcome.main.entity.story.StoryFeedbackType
import com.wellcome.main.entity.story.StoryType
import com.wellcome.main.service.extentions.generators.api.common.generateStoryDtoV1List
import com.wellcome.main.service.facade.story.StoryFeedbackService
import com.wellcome.main.service.facade.story.StoryService
import com.wellcome.main.service.facade.user.UserService
import com.wellcome.main.service.interceptor.UserInterceptorService
import com.wellcome.main.wrapper.EntityWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime
import javax.persistence.EntityNotFoundException

interface StoryManagementService {
    fun getAll(googleUid: String?, reviewing: Boolean, type: String): StoryResponseV1
    fun like(googleUid: String, storyId: Long): LikeResponseV1
    fun dislike(googleUid: String, storyId: Long): LikeResponseV1
    fun notInteresting(googleUid: String, storyId: Long): LikeResponseV1
}

@Service
open class DefaultStoryManagementService @Autowired constructor(
    private val userService: UserService,
    private val storyService: StoryService,
    private val storyFeedbackService: StoryFeedbackService,
    private val userInterceptorService: UserInterceptorService
) : StoryManagementService {

    @Transactional(readOnly = true)
    override fun getAll(googleUid: String?, reviewing: Boolean, type: String): StoryResponseV1 {
        val user = googleUid?.let {
            userService.findByGoogleUid(it)
                ?: throw EntityNotFoundException("User with googleUid: $googleUid is not found to database")
        }
        val stories = storyService.findAll()
            .filterNot { it.reviewForbiddenContent && reviewing }
            .filter { it.type == StoryType.valueOf(type) }
            .sortedByDescending {
                (it.updateEntityDateTime ?: it.createEntityDateTime)
                    .let(::requireNotNull)
                    .let(ZonedDateTime::parse)
            }.map { EntityWrapper(it) }
            .let { if (user != null) userInterceptorService.handleLikedStory(user, it) else it }
        return StoryResponseV1(stories.generateStoryDtoV1List())
    }

    @Transactional
    override fun like(googleUid: String, storyId: Long): LikeResponseV1 {
        val user = userService.findByGoogleUid(googleUid)
            ?: throw EntityNotFoundException("User width googleUid: $googleUid is not found to database")

        val story = storyService.findById(storyId)

        var storyFeedback =
            storyFeedbackService.findByUserAndStoryId(user.id!!, storyId)

        var isNew = false

        if (storyFeedback == null) {
            storyFeedback = StoryFeedback(StoryFeedbackType.LIKE, user, story)
            isNew = true
        } else {
            if (storyFeedback.type == StoryFeedbackType.LIKE) throw RuntimeException("Story already liked")
            else storyFeedback.apply { this.type = StoryFeedbackType.LIKE }
        }

        storyFeedbackService.saveOrUpdate(storyFeedback)

        val likeCount = story.storyFeedback
            .filter { it.type == StoryFeedbackType.LIKE }.size
            .let { return@let if (isNew) it.inc() else it }

        return LikeResponseV1(likeCount.toLong(), true)
    }

    @Transactional
    override fun dislike(googleUid: String, storyId: Long): LikeResponseV1 {
        val user = userService.findByGoogleUid(googleUid)
            ?: throw EntityNotFoundException("User width googleUid: $googleUid is not found to database")

        val storyFeedback = storyFeedbackService.findByUserAndStoryId(user.id!!, storyId)
            ?: throw Exception("Feedback is not found")

        if (storyFeedback.type == StoryFeedbackType.LIKE) {
            storyFeedbackService.deleteById(storyFeedback.id!!)
        } else throw RuntimeException("Story not liked")

        val likeCount = storyFeedback.story.storyFeedback
            .filter { it.type == StoryFeedbackType.LIKE }.size.dec()

        return LikeResponseV1(likeCount.toLong(), false)
    }

    @Transactional
    override fun notInteresting(googleUid: String, storyId: Long): LikeResponseV1 {
        val user = userService.findByGoogleUid(googleUid)
            ?: throw EntityNotFoundException("User width googleUid: $googleUid is not found to database")

        val story = storyService.findById(storyId)

        var storyFeedback =
            storyFeedbackService.findByUserAndStoryId(user.id!!, storyId)

        if (storyFeedback == null) {
            storyFeedback = StoryFeedback(StoryFeedbackType.NOT_INTERESTING, user, story)
        } else {
            if (storyFeedback.type == StoryFeedbackType.NOT_INTERESTING) throw RuntimeException("Story already check that not interesting")
            else storyFeedback.apply { this.type = StoryFeedbackType.NOT_INTERESTING }
        }

        storyFeedbackService.saveOrUpdate(storyFeedback)

        val likeCount = story.storyFeedback
            .filter { it.type == StoryFeedbackType.LIKE }.size

        return LikeResponseV1(likeCount.toLong(), false)
    }
}