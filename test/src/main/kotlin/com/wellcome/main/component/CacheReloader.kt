package com.wellcome.main.component

import com.wellcome.main.configuration.utils.CacheState
import com.wellcome.main.configuration.utils.CustomCacheManager
import com.wellcome.main.entity.Locality
import com.wellcome.main.service.facade.LocalityService
import com.wellcome.main.service.facade.institution.InstitutionService
import com.wellcome.main.service.utils.LogMessage
import com.wellcome.main.service.utils.LoggerService
import com.wellcome.main.util.enumerators.InstitutionCacheMethodNames
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.ZonedDateTime
import java.util.concurrent.Semaphore

interface CacheReloader {
    fun reloadAllCaches()
    fun reloadInstitutionServiceCache(localityId: Long)
}

@Component
open class DefaultCacheReloader @Autowired constructor(
    private val cacheState: CacheState,
    private val loggerService: LoggerService,
    private val localityService: LocalityService,
    private val timestampProvider: TimestampProvider,
    private val customCacheManager: CustomCacheManager,
    private val institutionService: InstitutionService
) : CacheReloader {

    private val changeQueue = Semaphore(1)

    override fun reloadAllCaches() {
        localityService.findAll()
            .filter { it.timezone != "Future" }
            .mapNotNull(Locality::id)
            .forEach(this::reloadInstitutionServiceCache)
    }

    override fun reloadInstitutionServiceCache(localityId: Long) {
        changeQueue.acquire()
        cacheState.lockCache()
        try {
            val locality = localityService.findById(localityId)
            val localityZonedDateTime = timestampProvider.getUserZonedDateTimeByTimeZoneId(locality.timezone)
            val institutions = institutionService.findByLocality(localityId)
                .filter { it.createEntityDateTime != null }
            val newInstitution = institutions
                .map { Pair(it.updateEntityDateTime ?: it.createEntityDateTime, it) }
                .map { Pair(requireNotNull(it.first), it.second) }
                .firstOrNull {
                    requireNotNull(it.first).let(ZonedDateTime::parse).plusMinutes(1) > localityZonedDateTime
                }?.second
            if (newInstitution != null) {
                customCacheManager.putValueInCache(
                    locality.name,
                    InstitutionCacheMethodNames.FIND_BY_ID.getKey(newInstitution.getIdNotNull()),
                    newInstitution)
            }
            if (changeQueue.queueLength == 0) {
                if (institutions.isNotEmpty()) {
                    customCacheManager.putValueInCache(
                        locality.name,
                        InstitutionCacheMethodNames.FIND_BY_LOCALITY.getKey(localityId),
                        institutions)
                }
                cacheState.unlockCache()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            loggerService.sendLogDeveloper(LogMessage("Trouble width reload cache method - ${e.message}"))
        } finally {
            changeQueue.release()
        }
    }

}