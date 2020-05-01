package com.wellcome.main.entity.story

import com.wellcome.main.entity.BaseEntity
import com.wellcome.main.entity.user.User
import javax.persistence.*

@Entity
@Table(name = "user_story")
class UserStory(

    @Column(name = "title", nullable = false)
    var title: String,

    @Column(name = "text", nullable = false)
    var text: String,

    @Column(name = "picture_url", nullable = false)
    var pictureUrl: String,

    @Column(name = "approved", nullable = false)
    var approved: Boolean = false,

    @OneToOne
    @JoinColumn(name = "user_id")
    var user: User

) : BaseEntity()