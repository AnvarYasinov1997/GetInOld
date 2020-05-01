package com.wellcome.main.entity.story

import com.wellcome.main.entity.BaseEntity
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption
import javax.persistence.*

@Entity
@Table(name = "stories")
class Story(

    @Column(name = "title", nullable = false)
    var title: String,

    @Column(name = "text", nullable = false)
    var text: String,

    @Column(name = "review_forbidden_content")
    var reviewForbiddenContent: Boolean,

    @Column(name = "picture_url", nullable = false)
    var pictureUrl: String,

    @Enumerated(value = EnumType.STRING)
    @Column(name = "type")
    var type: StoryType,

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "story")
    var storyFeedback: List<StoryFeedback> = mutableListOf()

) : BaseEntity()

enum class StoryType {
    USEFUL, INTERESTING
}

