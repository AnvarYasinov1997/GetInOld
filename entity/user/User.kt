package com.wellcome.main.entity.user

import com.wellcome.main.entity.BaseEntity
import com.wellcome.main.entity.Bookmark
import com.wellcome.main.entity.institutionProfile.InstitutionProfile
import com.wellcome.main.entity.Locality
import com.wellcome.main.entity.institution.Institution
import com.wellcome.main.entity.institution.InstitutionEvent
import com.wellcome.main.entity.institution.InstitutionReview
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption
import java.time.LocalDate
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "users")
class User(

    @Column(name = "google_uid", nullable = false, unique = true)
    var googleUid: String,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "push_available", nullable = false)
    var pushAvailable: Boolean = true,

    @Column(name = "date_of_birth")
    var dateOfBirth: LocalDate? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    var gender: Gender = Gender.NOT_DETERMINE,

    @Column(name = "birthday")
    var birthday: String? = null,

    @OneToOne
    @JoinColumn(name = "session_id")
    var session: Session? = null,

    @Column(name = "email")
    var email: String?,

    @Column(name = "photo_url")
    var photoUrl: String,

    @ManyToOne
    @JoinColumn(name = "locality_id", nullable = false)
    var locality: Locality,

    @ManyToOne
    @JoinColumn(name = "role_id")
    var role: Role = Role(DEFAULT_USER_ROLE).also {
        it.id = DEFAULT_USER_ROLE_ID
    },

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "user")
    var reviews: MutableList<InstitutionReview> = mutableListOf(),

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "user")
    var bookmarks: MutableList<Bookmark> = mutableListOf(),

    @Column(name = "blocked")
    var blocked: Boolean = false,

    @Column(name = "password", nullable = false)
    private var password: String = UUID.randomUUID().toString(),

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_institution_profiles",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "institution_profile_id", referencedColumnName = "id")])
    val institutionProfiles: MutableSet<InstitutionProfile> = mutableSetOf()

) : BaseEntity() {

    fun getInstitutionBookmarks(): List<Institution> =
        this.bookmarks.mapNotNull(Bookmark::institution).filterNot(Institution::blocked)

    fun getEventBookmarks(): List<InstitutionEvent> =
        this.bookmarks.mapNotNull(Bookmark::event).filterNot(InstitutionEvent::completed)

    fun getInstitutionReviews(): List<Institution> =
        this.reviews.map(InstitutionReview::institution).filterNot(Institution::blocked)

    companion object {
        private const val DEFAULT_USER_ROLE = "user"
        private const val DEFAULT_USER_ROLE_ID = 1L
    }

}

enum class Gender {
    MALE, FEMALE, NOT_DETERMINE;
}