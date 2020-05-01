package com.wellcome.main.entity.institutionProfile

import com.wellcome.main.entity.BaseEntity
import com.wellcome.main.entity.institution.InstitutionPicture
import javax.persistence.*

@Entity
@Table(name = "institution_edit_request_pictures")
class InstitutionEditRequestPicture(

    @OneToOne
    @JoinColumn(name = "institution_picture_id", nullable = false, unique = true)
    var picture: InstitutionPicture,

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    var status: InstitutionEditRequestContentStatus,

    @ManyToOne
    @JoinColumn(name = "institution_edit_request_id", nullable = false)
    var institutionEditRequest: InstitutionEditRequest

) : BaseEntity()