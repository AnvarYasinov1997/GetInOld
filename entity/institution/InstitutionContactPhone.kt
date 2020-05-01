package com.wellcome.main.entity.institution

import com.wellcome.main.entity.BaseEntity
import javax.persistence.*

@Entity
@Table(name = "institution_contact_phones")
class InstitutionContactPhone(

    @Column(name = "phone_number", nullable = false)
    var phoneNumber: String,

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    var institution: Institution

) : BaseEntity()