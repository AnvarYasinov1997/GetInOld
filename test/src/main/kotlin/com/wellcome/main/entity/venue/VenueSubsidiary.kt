package com.wellcome.main.entity.venue

import com.wellcome.main.entity.BaseEntity
import com.wellcome.main.entity.venue.offer.Offer
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption
import javax.persistence.*

@Entity
@Table(name = "venue_subsidiaries")
class VenueSubsidiary(

    @Column(name = "name", nullable = false)
    var name: String,

    @OneToMany(mappedBy = "subsidiary")
    @LazyCollection(LazyCollectionOption.FALSE)
    var venues: MutableList<Venue> = mutableListOf(),

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(
        name = "venue_subsidiaries_offers",
        joinColumns = [JoinColumn(name = "venue_subsidiary_id", nullable = false)],
        inverseJoinColumns = [JoinColumn(name = "offer_id", nullable = false)])
    var offers: MutableSet<Offer> = mutableSetOf()

) : BaseEntity() {


    fun getNotCompletedOffers(): List<Offer> =
        this.offers.filterNot(Offer::completed)


}