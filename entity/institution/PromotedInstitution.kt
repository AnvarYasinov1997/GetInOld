package com.wellcome.main.entity.institution

import com.wellcome.main.entity.BaseEntity
import javax.persistence.*

@Entity
@Table(name = "promoted_institutions")
class PromotedInstitution(

    @ManyToOne
    @JoinColumn(name = "institution_id")
    var institution: Institution,

    @OneToOne
    @JoinColumn(name = "institution_category_id")
    var institutionCategory: InstitutionCategory

) : BaseEntity()