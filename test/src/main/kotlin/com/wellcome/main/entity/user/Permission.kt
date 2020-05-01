package com.wellcome.main.entity.user

import com.wellcome.main.entity.BaseEntity
import org.springframework.security.core.GrantedAuthority
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "permissions")
class Permission(

    @Column(name = "name", unique = true, nullable = false)
    var name: String

) : GrantedAuthority, BaseEntity() {

    override fun toString(): String {
        return name
    }

    override fun getAuthority(): String = name

}