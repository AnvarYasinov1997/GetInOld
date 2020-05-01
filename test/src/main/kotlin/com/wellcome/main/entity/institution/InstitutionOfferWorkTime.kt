package com.wellcome.main.entity.institution

import com.wellcome.main.entity.BaseEntity
import javax.persistence.*

@Entity
@Table(name = "institution_offer_work_times")
class InstitutionOfferWorkTime(

    @Enumerated(value = EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    var dayOfWeek: DayOfWeeks,

    @Column(name = "start_time", nullable = false)
    var startTime: String,

    @Column(name = "end_time", nullable = false)
    var endTime: String,

    @ManyToOne
    @JoinColumn(name = "institution_offer_id", nullable = false)
    var institutionOffer: InstitutionOffer

) : BaseEntity()