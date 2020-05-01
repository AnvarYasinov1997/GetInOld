package com.wellcome.main.entity.institutionProfile

import com.wellcome.main.entity.BaseEntity
import com.wellcome.main.entity.institution.Institution
import javax.persistence.*

@Table
@Entity(name = "institution_profile")
class InstitutionProfile(

    @OneToOne
    @JoinColumn(name = "institution_id", nullable = false)
    var institution: Institution,

    @Column(name = "login", nullable = false)
    var login: String,

    @Column(name = "access_key", nullable = false)
    var accessKey: String

) : BaseEntity()