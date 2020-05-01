package com.wellcome.main.entity.venue


import com.wellcome.main.entity.BaseEntity
import com.wellcome.main.entity.user.User
import javax.persistence.*

@Entity
@Table(name = "reviews")
class Review(

    @Column(name = "feedback", nullable = false)
    var feedback: String,

    @Column(name = "start_count", nullable = false)
    var startCount: Long,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @ManyToOne
    @JoinColumn(name = "venue_id", nullable = false)
    var venue: Venue

) : BaseEntity()