package com.wellcome.main.entity.venue.offer

import com.wellcome.main.entity.BaseEntity
import com.wellcome.main.entity.institution.DayOfWeeks
import javax.persistence.*

@Entity
@Table(name = "offer_work_times")
class OfferWorkTime(

    @Enumerated(value = EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    var dayOfWeek: DayOfWeeks,

    @Column(name = "start_work", nullable = false)
    var startWork: String,

    @Column(name = "end_work", nullable = false)
    var endWork: String,

    @Column(name = "closed", nullable = false)
    var closed: Boolean,

    @ManyToOne
    @JoinColumn(name = "offer_id", nullable = false)
    var offer: Offer

) : BaseEntity()