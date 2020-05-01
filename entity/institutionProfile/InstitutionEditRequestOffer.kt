package com.wellcome.main.entity.institutionProfile

import com.wellcome.main.entity.BaseEntity
import com.wellcome.main.entity.institution.InstitutionOffer
import javax.persistence.*

@Entity
@Table(name = "institution_edit_request_offers")
class InstitutionEditRequestOffer(

    @OneToOne
    @JoinColumn(name = "institution_offer_id", nullable = false, unique = true)
    var offer: InstitutionOffer,

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    var status: InstitutionEditRequestContentStatus,

    @ManyToOne
    @JoinColumn(name = "institution_edit_request_id", nullable = false)
    var institutionEditRequest: InstitutionEditRequest

) : BaseEntity()