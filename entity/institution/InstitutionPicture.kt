package com.wellcome.main.entity.institution

import com.wellcome.main.entity.BaseEntity
import javax.persistence.*

@Entity
@Table(name = "institution_pictures")
class InstitutionPicture(

    @Column(name = "picture_url", nullable = false)
    var pictureUrl: String,

    @Column(name = "in_review", nullable = false)
    var inReview: Boolean,

    @ManyToOne
    @JoinColumn(name = "institution_id")
    var institution: Institution

) : BaseEntity()