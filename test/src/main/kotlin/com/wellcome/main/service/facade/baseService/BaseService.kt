package com.wellcome.main.service.facade.baseService

import com.wellcome.main.entity.BaseEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityNotFoundException

interface BaseServiceSerializable

interface BaseService<T : BaseEntity> : BaseServiceSerializable {
    fun findById(id: Long): T
    fun findAll(): List<T>
    fun saveAll(entityList: List<T>): List<T>
    fun saveOrUpdate(entity: T): T
    fun deleteById(id: Long)
    fun findAllIdList(): List<Long>
}

open class DefaultBaseService<T : BaseEntity>(private val entityName: String,
                                              private val repository: JpaRepository<T, Long>) : BaseService<T> {
    @Transactional(readOnly = true)
    override fun findById(id: Long): T = repository.findById(id).orElseThrow {
        EntityNotFoundException("$entityName width id: $id is not found to database")
    }

    @Transactional(readOnly = true)
    override fun findAll(): List<T> =
        repository.findAll()

    @Transactional
    override fun saveAll(entityList: List<T>): List<T> =
        repository.saveAll(entityList)

    @Transactional
    override fun saveOrUpdate(entity: T): T = repository.save(entity)

    @Transactional
    override fun deleteById(id: Long) {
        repository.deleteById(id)
    }

    @Transactional(readOnly = true)
    override fun findAllIdList(): List<Long> =
        repository.findAll()
            .mapNotNull(BaseEntity::id)
            .sorted()
}