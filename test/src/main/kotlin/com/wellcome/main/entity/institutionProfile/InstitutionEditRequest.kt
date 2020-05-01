package com.wellcome.main.entity.institutionProfile

import com.wellcome.main.entity.BaseEntity
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption
import javax.persistence.*

@Entity
@Table(name = "institution_edit_requests")
class InstitutionEditRequest(

    @Column(name = "avatar_url", nullable = false)
    var avatarUrl: String,

    @Column(name = "description", nullable = false)
    var description: String,

    @Column(name = "approved", nullable = false)
    var approved: Boolean = false,

    @OneToOne
    @JoinColumn(name = "institution_edit_request_status_id", nullable = false)
    var status: InstitutionEditRequestStatus,

    @OneToOne
    @JoinColumn(name = "institution_profile_id", nullable = false)
    var institutionProfile: InstitutionProfile,

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "institutionEditRequest")
    var events: List<InstitutionEditRequestEvent> = mutableListOf(),

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "institutionEditRequest")
    var offers: List<InstitutionEditRequestOffer> = mutableListOf(),

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "institutionEditRequest")
    var pictures: List<InstitutionEditRequestPicture> = mutableListOf()

) : BaseEntity()