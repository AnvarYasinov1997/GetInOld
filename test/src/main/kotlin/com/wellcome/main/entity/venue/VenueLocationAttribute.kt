package com.wellcome.main.entity.venue

import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class VenueLocationAttribute(

    @Column(name = "lat", nullable = false)
    var lat: Double,

    @Column(name = "lon", nullable = false)
    var lon: Double,

    @Column(name = "address", nullable = false)
    var address: String

)