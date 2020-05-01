package com.wellcome.main.entity.venue

import com.wellcome.main.entity.BaseEntity
import javax.persistence.*

@Entity
@Table(name = "pictures")
class Picture(

    @Column(name = "picture_url", nullable = false)
    var pictureUrl: String,

    @Column(name = "in_review", nullable = false)
    var inReview: Boolean,

    @ManyToOne
    @JoinColumn(name = "venue_id", nullable = false)
    var venue: Venue

) : BaseEntity()