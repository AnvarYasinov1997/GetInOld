package com.wellcome.main.entity.venue.event

import com.wellcome.main.entity.BaseEntity
import com.wellcome.main.entity.Price
import com.wellcome.main.entity.venue.Venue
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption
import javax.persistence.*

@Entity
@Table(name = "events")
class Event(

    @Column(name = "picture_url", nullable = false)
    var pictureUrl: String,

    @Column(name = "start_work", nullable = false)
    var startWork: String,

    @Column(name = "start_date", nullable = false)
    var startDate: String,

    @Column(name = "completed", nullable = false)
    var completed: Boolean,

    @Column(name = "in_review", nullable = false)
    var inReview: Boolean,

    @OneToOne
    @JoinColumn(name = "price_id", nullable = false)
    var price: Price,

    @ManyToOne
    @JoinColumn(name = "venue_id", nullable = false)
    var venue: Venue,

    @OneToMany(mappedBy = "event")
    @LazyCollection(LazyCollectionOption.FALSE)
    var eventContents: List<EventContent> = mutableListOf()

) : BaseEntity()