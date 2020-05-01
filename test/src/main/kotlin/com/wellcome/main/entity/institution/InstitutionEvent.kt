package com.wellcome.main.entity.institution

import com.wellcome.main.entity.BaseEntity
import com.wellcome.main.entity.Price
import javax.persistence.*

@Entity
@Table(name = "institution_event")
class InstitutionEvent(

    @Column(name = "title", nullable = false)
    var title: String,

    @Column(name = "picture_url", nullable = false)
    var pictureUrl: String,

    @Column(name = "description", nullable = false)
    var description: String,

    @Column(name = "start_work", nullable = false)
    var startWork: String,

    @Column(name = "date", nullable = false)
    var date: String,

    @Column(name = "square", nullable = false)
    var square: Boolean,

    @Column(name = "promoted", nullable = false)
    var promoted: Boolean = false,

    @Column(name = "completed", nullable = false)
    var completed: Boolean,

    @Column(name = "in_review", nullable = false)
    var inReview: Boolean,

    @OneToOne
    @JoinColumn(name = "price_id")
    var price: Price,

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    var institution: Institution

) : BaseEntity()