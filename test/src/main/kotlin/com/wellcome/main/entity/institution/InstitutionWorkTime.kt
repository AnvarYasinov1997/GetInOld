package com.wellcome.main.entity.institution

import com.wellcome.main.entity.BaseEntity
import javax.persistence.*

@Entity
@Table(name = "institution_work_time")
class InstitutionWorkTime(

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
    @JoinColumn(name = "institution_id", nullable = false)
    var institution: Institution

) : BaseEntity()

enum class DayOfWeeks {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;

    fun decrementDayOfWeek(): DayOfWeeks {
        return when (this) {
            TUESDAY -> MONDAY
            WEDNESDAY -> TUESDAY
            THURSDAY -> WEDNESDAY
            FRIDAY -> THURSDAY
            SATURDAY -> FRIDAY
            SUNDAY -> SATURDAY
            else -> SUNDAY
        }
    }

    fun incrementDayOfWeek(): DayOfWeeks {
        return when (this) {
            MONDAY -> TUESDAY
            TUESDAY -> WEDNESDAY
            WEDNESDAY -> THURSDAY
            THURSDAY -> FRIDAY
            FRIDAY -> SATURDAY
            SATURDAY -> SUNDAY
            else -> MONDAY
        }
    }

}