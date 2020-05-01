package com.wellcome.main.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "localities")
class Locality(

    @Column(name = "name", nullable = false, unique = true)
    var name: String,

    @Column(name = "timezone", nullable = false)
    var timezone: String,

    @Column(name = "topic")
    var topic: String

) : BaseEntity()