package com.wellcome.main.entity.institution

import com.wellcome.main.entity.BaseEntity
import com.wellcome.main.entity.Locality
import com.wellcome.main.entity.user.User
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "institutions")
class Institution(

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "description", nullable = false)
    var description: String,

    @Column(name = "comments")
    var comments: String?,

    @Column(name = "rating", nullable = false)
    var rating: Double,

    @Column(name = "people_of_rated_count", nullable = false)
    var peopleOfRatedCount: Long,

    @Column(name = "avatar_url", nullable = false)
    var avatarUrl: String,

    @Column(name = "instagram_account")
    var instagramAccount: String?,

    @Embedded
    var locationAttributes: InstitutionLocationAttributes,

    @ManyToOne
    @JoinColumn(name = "locality_id", nullable = false)
    var locality: Locality,

    @OneToOne
    @JoinColumn(name = "moderated_user_id", nullable = false)
    var moderator: User,

    @OneToOne
    @JoinColumn(name = "subsidiary_id")
    var subsidiary: InstitutionSubsidiary? = null,

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "institution")
    var offers: MutableList<InstitutionOffer> = mutableListOf(),

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "institution")
    var pictures: MutableList<InstitutionPicture> = mutableListOf(),

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "institution")
    var workTime: MutableList<InstitutionWorkTime> = mutableListOf(),

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "institution")
    var reviews: MutableList<InstitutionReview> = mutableListOf(),

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "institution")
    var contactPhones: MutableList<InstitutionContactPhone> = mutableListOf(),

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "institution")
    var promoted: MutableList<PromotedInstitution> = mutableListOf(),

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "institution")
    var events: MutableList<InstitutionEvent> = mutableListOf(),

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "institution")
    var tags: MutableList<InstitutionTag> = mutableListOf(),

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany
    @JoinTable(name = "institution_institution_categories",
        joinColumns = [JoinColumn(name = "institution_id", nullable = false)],
        inverseJoinColumns = [JoinColumn(name = "institution_category_id", nullable = false)])
    var categories: MutableSet<InstitutionCategory>,

    @Column(name = "blocked", nullable = false)
    var blocked: Boolean,

    @Column(name = "processing", nullable = false)
    var processing: Boolean,

    @Column(name = "approved", nullable = false)
    var approved: Boolean,

    @Column(name = "ranging", nullable = false)
    var ranging: Boolean,

    @Enumerated(value = EnumType.STRING)
    @Column(name = "priority", nullable = false)
    var priority: InstitutionPriority = InstitutionPriority.HIGH

) : BaseEntity() {

    fun getSubsidiaryOffers(): List<InstitutionOffer> =
        this.subsidiary?.getNotCompletedOffers()?.toMutableList() ?: emptyList()

    fun checkPromotedByCategory(categoryType: InstitutionCategoryType): Boolean {
        for (i in this.promoted) {
            if (i.institutionCategory.categoryType == categoryType) return true
        }
        return false
    }

    fun getWorkingEvents(): List<InstitutionEvent> =
        this.events.filterNot(InstitutionEvent::completed)
            .filterNot(InstitutionEvent::inReview)

    fun getWorkingOffers(): List<InstitutionOffer> =
        this.offers.filterNot(InstitutionOffer::completed)
            .filterNot(InstitutionOffer::inReview)

}

enum class InstitutionPriority {
    LOW, HIGH;
}