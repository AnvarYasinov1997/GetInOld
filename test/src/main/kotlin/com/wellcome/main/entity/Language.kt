package com.wellcome.main.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "languages")
class Language(

    @Column(name = "name")
    var name: String

) : BaseEntity()