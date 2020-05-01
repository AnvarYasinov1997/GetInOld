package com.wellcome.main.entity

import javax.persistence.Id
import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class BaseEntity(@Id var id: Long? = null) {

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other === this) return true
        if (other.javaClass != javaClass) return false
        other as BaseEntity
        return (this.id == other.id)
    }

    override fun hashCode(): Int {
        return id!!.toInt()
    }

}