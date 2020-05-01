package com.wellcome.main.entity.user

import com.wellcome.main.entity.BaseEntity
import javax.persistence.*

@Entity
@Table(name = "roles")
class Role(

    @Column(name = "name", nullable = false)
    var name: String,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "roles_permissions",
        joinColumns = [(JoinColumn(name = "role_id", referencedColumnName = "id"))],
        inverseJoinColumns = [(JoinColumn(name = "permission_id", referencedColumnName = "id"))]
    )
    var permissions: MutableSet<Permission> = mutableSetOf()

) : BaseEntity()