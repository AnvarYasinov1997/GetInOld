package com.wellcome.main.entity.venue

import com.wellcome.main.entity.BaseEntity
import com.wellcome.main.entity.institution.Institution
import javax.persistence.*

@Entity
@Table(name = "contact_phones")
class ContactPhone(

    @Column(name = "phone_number", nullable = false)
    var phoneNumber: String,

    @ManyToOne
    @JoinColumn(name = "venue_id", nullable = false)
    var venue: Venue

) : BaseEntity()