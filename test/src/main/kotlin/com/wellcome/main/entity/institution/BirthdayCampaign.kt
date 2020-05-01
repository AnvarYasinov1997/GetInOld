package com.wellcome.main.entity.institution

import com.wellcome.main.entity.BaseEntity
import com.wellcome.main.entity.user.Gender
import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "birthday_campaigns")
class BirthdayCampaign(

    @Column(name = "text", nullable = false)
    var text: String,

    @Enumerated(value = EnumType.STRING)
    @Column(name = "gender", nullable = false)
    var gender: Gender,

    @Column(name = "age", nullable = false)
    var age: String,

    @Column(name = "expiration_date", nullable = false)
    var expirationDate: LocalDate,

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: BirthdayCampaignStatus = BirthdayCampaignStatus.REVIEW,

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    var institution: Institution

) : BaseEntity()

enum class BirthdayCampaignStatus {
    ACTIVE, PAUSED, DELETED, REVIEW, DECLINED, EXPIRED
}