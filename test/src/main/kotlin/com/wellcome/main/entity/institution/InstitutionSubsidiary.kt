package com.wellcome.main.entity.institution

import com.wellcome.main.entity.BaseEntity
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption
import javax.persistence.*

@Entity
@Table(name = "institution_subsidiary")
class InstitutionSubsidiary(

    @Column(name = "name", nullable = false)
    var name: String,

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "subsidiary")
    var institutions: MutableList<Institution> = mutableListOf(),

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany
    @JoinTable(name = "subsidiary_offers",
        joinColumns = [JoinColumn(name = "subsidiary_id", nullable = false)],
        inverseJoinColumns = [JoinColumn(name = "offer_id", nullable = false)])
    var institutionOffers: MutableSet<InstitutionOffer> = mutableSetOf()

) : BaseEntity() {

    fun getNotCompletedOffers(): List<InstitutionOffer> =
        this.institutionOffers.filterNot(InstitutionOffer::completed)

}