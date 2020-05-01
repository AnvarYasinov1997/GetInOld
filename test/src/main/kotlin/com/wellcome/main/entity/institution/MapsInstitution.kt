package com.wellcome.main.entity.institution

import com.wellcome.main.entity.BaseEntity
import com.wellcome.main.entity.Locality
import javax.persistence.*

@Entity
@Table(name = "maps_institutions")
class MapsInstitution(

    @Column(name = "name")
    var name: String,

    @OneToOne
    @JoinColumn(name = "locality_id", nullable = false)
    var locality: Locality,

    @Column(name = "address")
    var address: String?,

    @Column(name = "phones")
    var phones: String?,

    @Column(name = "emails")
    var emails: String?,

    @Column(name = "types")
    var types: String?,

    @Column(name = "cite")
    var cite: String?,

    @Column(name = "vk")
    var vk: String?,

    @Column(name = "payment_types")
    var paymentTypes: String?,

    @Column(name = "facebook")
    var facebook: String?,

    @Column(name = "instagram")
    var instagram: String?,

    @Column(name = "lat", nullable = false)
    var lat: Double,

    @Column(name = "lon", nullable = false)
    var lon: Double,

    @Column(name = "created", nullable = false)
    var created: Boolean = false

) : BaseEntity()