package com.wellcome.main.entity.venue

import com.wellcome.main.entity.BaseEntity
import javax.persistence.*

@Entity
@Table(name = "tags")
class Tag(

    @Column(name = "name")
    var name: String,

    @ManyToOne
    @JoinColumn(name = "venue_id", nullable = false)
    var venue: Venue

) : BaseEntity()