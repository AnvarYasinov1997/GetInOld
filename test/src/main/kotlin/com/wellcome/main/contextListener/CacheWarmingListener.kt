package com.wellcome.main.contextListener

import com.wellcome.main.configuration.utils.CustomCacheManager
import com.wellcome.main.entity.Locality
import com.wellcome.main.service.facade.LocalityService
import com.wellcome.main.service.facade.institution.InstitutionService
import com.wellcome.main.service.utils.LogMessage
import com.wellcome.main.service.utils.LoggerService
import com.wellcome.main.util.enumerators.InstitutionCacheMethodNames
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
open class CacheWarmingListener @Autowired constructor(
    private val loggerService: LoggerService,
    private val localityService: LocalityService,
    private val institutionService: InstitutionService,
    private val customCacheManager: CustomCacheManager
) {

    @EventListener(value = [ContextRefreshedEvent::class])
    open fun warmCache() {
        val localities = localityService.findAll()
        this.warmInstitutionCache(localities)
        loggerService.info(LogMessage("CacheWarmingListener warm all caches"))
    }

    private fun warmInstitutionCache(localities: List<Locality>) {
        localities.forEach { locality ->
            val institutions = institutionService.findByLocality(locality.getIdNotNull())
            customCacheManager.putValueInCache(
                locality.name,
                InstitutionCacheMethodNames.FIND_BY_LOCALITY.getKey(locality.getIdNotNull()),
                institutions)
            institutions.forEach { institution ->
                val key = InstitutionCacheMethodNames.FIND_BY_ID.getKey(institution.getIdNotNull())
                customCacheManager.putValueInCache(
                    locality.name,
                    key,
                    institution)
            }
        }
        loggerService.info(LogMessage("CacheWarmingListener warm institution caches"))
    }

}