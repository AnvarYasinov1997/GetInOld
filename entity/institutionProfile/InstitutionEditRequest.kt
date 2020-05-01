package com.wellcome.main.entity.institutionProfile

import com.wellcome.main.entity.BaseEntity
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption
import javax.persistence.*

@Entity
@Table(name = "institution_edit_requests")
class InstitutionEditRequest(

    @Column(name = "avatar_url")
    var avatarUrl: String,

    @Column(name = "description")
    var description: String,

    @Column(name = "approved")
    var approved: Boolean = false,

    @OneToOne
    @JoinColumn(name = "institution_edit_request_status_id")
    var status: InstitutionEditRequestStatus,

    @OneToOne
    @JoinColumn(name = "institution_profile_id")
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