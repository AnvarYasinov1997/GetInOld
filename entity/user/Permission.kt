package com.wellcome.main.entity.user

import com.wellcome.main.entity.BaseEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "permissions")
class Permission(

    @Column(name = "name", unique = true, nullable = false)
    var name: String

) : BaseEntity() {

    override fun toString(): String {
        return name
    }

}