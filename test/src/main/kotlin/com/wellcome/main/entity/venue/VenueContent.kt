package com.wellcome.main.entity.venue

import com.wellcome.main.entity.BaseEntity
import com.wellcome.main.entity.Language
import javax.persistence.*

@Entity
@Table(name = "venue_contents")
class VenueContent(

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "description", nullable = false)
    var description: String,

    @ManyToOne
    @JoinColumn(name = "language_id", nullable = false)
    var language: Language,

    @ManyToOne
    @JoinColumn(name = "venue_id", nullable = false)
    var venue: Venue

) : BaseEntity()