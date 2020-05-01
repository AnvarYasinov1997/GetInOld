package com.wellcome.main.entity.venue

import com.wellcome.main.entity.BaseEntity
import com.wellcome.main.entity.institution.DayOfWeeks
import javax.persistence.*

@Entity
@Table(name = "venue_work_times")
class VenueWorkTime(

    @Column(name = "start_work", nullable = false)
    var startDay: String,

    @Column(name = "end_work", nullable = false)
    var endDay: String,

    @Column(name = "closed", nullable = false)
    var closed: Boolean,

    @Enumerated(value = EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    var dayOfWeek: DayOfWeeks,

    @ManyToOne
    @JoinColumn(name = "venue_id", nullable = false)
    var venue: Venue

) : BaseEntity()