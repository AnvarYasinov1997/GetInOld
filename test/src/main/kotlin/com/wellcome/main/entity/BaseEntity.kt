package com.wellcome.main.entity

import com.wellcome.main.configuration.utils.ThreadLocalConfiguration
import org.hibernate.annotations.GenericGenerator
import java.time.ZonedDateTime
import javax.persistence.*

@MappedSuperclass
abstract class BaseEntity {

    @Id
    @GenericGenerator(name = "custom_sequence_generator", strategy = "com.wellcome.main.databaseUtils.CustomSequenceGenerator")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "custom_sequence_generator")
    var id: Long? = null

    @Column(name = "create_entity_date_time")
    var createEntityDateTime: String? = null

    @Column(name = "update_entity_date_time")
    var updateEntityDateTime: String? = null

    fun getIdNotNull(): Long = requireNotNull(this.id)

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other === this) return true
        if (other.javaClass != javaClass) return false
        other as BaseEntity
        return (this.id == other.id)
    }

    // -1 для тестов
    override fun hashCode(): Int {
        return id?.toInt() ?: -1
    }

    @PrePersist
    private fun prePersist() {
        this.createEntityDateTime = this.getTime().toString()
    }

    @PreUpdate
    private fun preUpdate() {
        this.updateEntityDateTime = this.getTime().toString()
    }

    private fun getTime(): ZonedDateTime? =
        ThreadLocalConfiguration
            .userZonedDateTimeRequestThreadLocal
            .getUserZonedDateTimeRequestThreadLocal()
            .get()

}