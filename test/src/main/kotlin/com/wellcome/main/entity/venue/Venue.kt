package com.wellcome.main.entity.venue

import com.wellcome.main.entity.BaseEntity
import com.wellcome.main.entity.Locality
import com.wellcome.main.entity.venue.event.Event
import com.wellcome.main.entity.venue.offer.Offer
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption
import javax.persistence.*

@Entity
@Table(name = "venues")
class Venue(

    @Column(name = "rating", nullable = false)
    var rating: Double,

    @Column(name = "number_of_people_rated", nullable = false)
    var numberOfPeopleRated: Long,

    @Column(name = "instagram_account")
    var instagramAccount: String?,

    @Column(name = "avatar_url", nullable = false)
    var avatarUrl: String,

    @Column(name = "blocked", nullable = false)
    var blocked: Boolean,

    @Embedded
    var locationAttribute: VenueLocationAttribute,

    @ManyToOne
    @JoinColumn(name = " locality_id", nullable = false)
    var locality: Locality,

    @OneToOne
    @JoinColumn(name = "subsidiary_id")
    var subsidiary: VenueSubsidiary? = null,

    @OneToMany(mappedBy = "venue")
    @LazyCollection(LazyCollectionOption.FALSE)
    var reviews: MutableList<Review> = mutableListOf(),

    @OneToMany(mappedBy = "venue")
    @LazyCollection(LazyCollectionOption.FALSE)
    var contents: MutableList<VenueContent> = mutableListOf(),

    @OneToMany(mappedBy = "venue")
    @LazyCollection(LazyCollectionOption.FALSE)
    var offers: MutableList<Offer> = mutableListOf(),

    @OneToMany(mappedBy = "venue")
    @LazyCollection(LazyCollectionOption.FALSE)
    var events: MutableList<Event> = mutableListOf(),

    @OneToMany(mappedBy = "venue")
    @LazyCollection(LazyCollectionOption.FALSE)
    var tags: MutableList<Tag> = mutableListOf(),

    @OneToMany(mappedBy = "venue")
    @LazyCollection(LazyCollectionOption.FALSE)
    var contactPhones: MutableList<ContactPhone> = mutableListOf(),

    @OneToMany(mappedBy = "venue")
    @LazyCollection(LazyCollectionOption.FALSE)
    var pictures: MutableList<Picture> = mutableListOf(),

    @OneToMany(mappedBy = "venue")
    @LazyCollection(LazyCollectionOption.FALSE)
    var venueWorkTimes: MutableList<VenueWorkTime> = mutableListOf(),

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany
    @JoinTable(name = "venues_categories",
        joinColumns = [JoinColumn(name = "venue_id", nullable = false)],
        inverseJoinColumns = [JoinColumn(name = "category_id", nullable = false)])
    var categories: MutableSet<Category> = mutableSetOf()

) : BaseEntity()