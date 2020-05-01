package com.wellcome.main.entity.venue.offer

import com.wellcome.main.entity.BaseEntity
import com.wellcome.main.entity.institution.OfferType
import com.wellcome.main.entity.venue.Venue
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption
import javax.persistence.*

@Entity
@Table(name = "offers")
class Offer(

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var type: OfferType,

    @Column(name = "picture_url", nullable = false)
    var pictureUrl: String,

    @Column(name = "birthday", nullable = false)
    var birthday: Boolean,

    @Column(name = "in_review", nullable = false)
    var inReview: Boolean,

    @Column(name = "completed", nullable = false)
    var completed: Boolean,

    @ManyToOne
    @JoinColumn(name = "venue_id")
    var venue: Venue?,

    @OneToMany(mappedBy = "offer")
    @LazyCollection(LazyCollectionOption.FALSE)
    var offerContent: List<OfferContent> = mutableListOf(),

    @OneToMany(mappedBy = "offer")
    @LazyCollection(LazyCollectionOption.FALSE)
    var workTimes: MutableList<OfferWorkTime> = mutableListOf()

) : BaseEntity() {

    fun getVenueNotNull(): Venue = requireNotNull(this.venue)

    fun getVenueRatingNotNull(): Double = requireNotNull(this.venue).rating

}