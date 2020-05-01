package com.wellcome.main.entity.selection

import com.wellcome.main.entity.BaseEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "selections")
class Selection(

    @Column(name = "name", unique = true)
    var name: String

) : BaseEntity()