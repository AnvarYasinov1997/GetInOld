package com.wellcome.main.databaseUtils

import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.BaseServiceSerializable
import com.wellcome.main.service.utils.LogMessage
import com.wellcome.main.service.utils.LoggerService
import com.wellcome.main.util.functions.ifNotEmpty
import org.springframework.aop.framework.Advised
import java.util.*
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

interface SequenceCacheUploader {
    fun uploadSequences()
}

interface SequenceCache {
    fun getNextId(entity: Any): Long
}

open class DefaultSequenceCache : SequenceCache, SequenceCacheUploader {

    lateinit var loggerService: LoggerService

    lateinit var serviceSerializables: List<BaseServiceSerializable>

    private lateinit var services: List<Pair<String, BaseService<*>>>

    private lateinit var entityRangeList: List<EntityRange>

    private lateinit var entityLocks: List<Pair<String, Lock>>

    override fun getNextId(entity: Any): Long {
        val entityName = entity.javaClass.simpleName
        val entityLock = entityLocks.first { it.first == entityName }
        entityLock.second.lock()
        val entityRange = this.entityRangeList.firstOrNull { it.entityName == entityName }
            ?: throw Exception("Entity width name $entityName does not match the architectural pattern")
        if (entityRange.freeIdRangeList.isEmpty()) {
            entityRange.lastId++
            entityLock.second.unlock()
            return entityRange.lastId
        }
        val entry = entityRange.freeIdRangeList.entries.first()
        val lowRange = entry.value.first
        val topRange = entry.value.last
        val id = lowRange + 1
        if (lowRange + 1 == topRange - 1) {
            entityRange.freeIdRangeList.remove(entry.key)
            entityLock.second.unlock()
            return id
        } else {
            val newRange = (lowRange + 1)..topRange
            entityRange.freeIdRangeList[entry.key] = newRange
            entityLock.second.unlock()
            return id
        }
    }

    override fun uploadSequences() {
        this.initServices()
        this.uploadEntityRangeList()
        this.entityLocks = this.entityRangeList
            .map { Pair(it.entityName, ReentrantLock()) }
    }

    private fun uploadEntityRangeList() {
        this.entityRangeList = this.services
            .map { Pair(it.first, it.second.findAllIdList()) }
            .map { Triple(it.first, it.second.ifNotEmpty()?.last()?.plus(1) ?: 1, it.second) }
            .map { Triple(it.first, it.second, it.third.getRanges()) }
            .map { EntityRange(it.first, it.second, it.third) }
        loggerService.info(LogMessage("Entity ranges uploaded"))
    }

    private fun initServices() {
        this.services = this.serviceSerializables
            .asSequence()
            .map { Pair(it, it as Advised) }
            .map { Pair(it.first, it.second.targetSource) }
            .map { Triple(it.first, it.second.target, it.second.targetClass) }
            .map { Triple(it.first, requireNotNull(it.second), requireNotNull(it.third).superclass) }
            .map { Triple(it.first, it.second, requireNotNull(it.third)) }
            .map { Triple(it.first, it.second, it.third.getDeclaredField("entityName")) }
            .map { triple ->
                triple.also {
                    it.third.isAccessible = true
                }
            }
            .map { Pair(it.third.get(it.second), it.first) }
            .map { Pair(requireNotNull(it.first), it.second as BaseService<*>) }
            .map { Pair(it.first as String, it.second) }
            .toList()
        loggerService.info(LogMessage("Services inited"))
    }

    private fun List<Long>.getRanges(): MutableMap<String, LongRange> {
        var lowRange = 0L
        val rangeList = mutableListOf<LongRange>()
        for (i in this) {
            if(i == lowRange + 1) lowRange++
            else {
                rangeList.add(lowRange..i)
                lowRange = i
            }
        }
        return rangeList.map { UUID.randomUUID().toString() to it }.toMap().toMutableMap()
    }

    data class EntityRange(val entityName: String,
                           var lastId: Long,
                           val freeIdRangeList: MutableMap<String, LongRange>)

}