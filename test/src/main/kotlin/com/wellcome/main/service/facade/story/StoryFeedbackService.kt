package com.wellcome.main.service.facade.story

import com.wellcome.main.entity.story.StoryFeedback
import com.wellcome.main.repository.local.postgre.StoryFeedbackRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface StoryFeedbackService : BaseService<StoryFeedback> {
    fun findByUserAndStoryId(userId: Long, storyId: Long): StoryFeedback?
}

@Service
open class DefaultStoryFeedbackService @Autowired constructor(
    private val storyFeedbackRepository: StoryFeedbackRepository
) : StoryFeedbackService,
    DefaultBaseService<StoryFeedback>(StoryFeedback::class.java.simpleName, storyFeedbackRepository) {

    @Transactional
    override fun findByUserAndStoryId(userId: Long, storyId: Long): StoryFeedback? =
        storyFeedbackRepository.findByUserIdAndStoryId(userId, storyId).orElseGet { null }

}