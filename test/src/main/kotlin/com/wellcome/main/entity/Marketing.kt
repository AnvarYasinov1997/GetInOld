package com.wellcome.main.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "marketings")
class Marketing(

    @Column(name = "title", nullable = false)
    var title: String,

    @Column(name = "text", nullable = false)
    var text: String,

    @Column(name = "picture_url", nullable = false)
    var pictureUrl: String

) : BaseEntity()