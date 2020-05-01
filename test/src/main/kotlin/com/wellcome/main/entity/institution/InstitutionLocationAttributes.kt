package com.wellcome.main.entity.institution

import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class InstitutionLocationAttributes(

    @Column(name = "lat", nullable = false)
    var lat: Double,

    @Column(name = "lon", nullable = false)
    var lon: Double,

    @Column(name = "address", nullable = false)
    var address: String

)