package com.wellcome.main.entity.story

import com.wellcome.main.entity.BaseEntity
import com.wellcome.main.entity.user.User
import javax.persistence.*

@Entity
@Table(name = "story_feedbacks")
class StoryFeedback(

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var type: StoryFeedbackType,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @ManyToOne
    @JoinColumn(name = "story_id", nullable = false)
    var story: Story

) : BaseEntity()

enum class StoryFeedbackType {
    LIKE, NOT_INTERESTING;

    fun isLiked(): Boolean {
        return when (this) {
            LIKE -> true
            NOT_INTERESTING -> false
        }
    }

}