package com.wellcome.main.service.cache

import com.wellcome.main.annotations.CacheKey
import com.wellcome.main.configuration.utils.CacheConfiguration
import com.wellcome.main.configuration.utils.CacheState
import com.wellcome.main.entity.institution.Institution
import com.wellcome.main.service.facade.institution.InstitutionService
import com.wellcome.main.service.utils.LogMessage
import com.wellcome.main.service.utils.LoggerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

interface InstitutionCacheService {
    fun findById(institutionId: Long): Institution
    fun findByLocality(localityId: Long): List<Institution>
}

@Service
open class DefaultInstitutionCacheService @Autowired constructor(
    private val cacheState: CacheState,
    private val loggerService: LoggerService,
    private val institutionService: InstitutionService
) : InstitutionCacheService {

    @Cacheable(
        cacheResolver = CacheConfiguration.APPLICATION_CACHE_KEY,
        keyGenerator = CacheConfiguration.SHA_256_KEY_GEN,
        condition = "!@cacheState.isCacheLocked() && @cacheState.isCacheEnabled()")
    override fun findById(@CacheKey institutionId: Long): Institution {
        if (cacheState.isCacheEnabled()) {
            loggerService.info(LogMessage("${this.javaClass.simpleName} caching new value by institutionId $institutionId"))
        }
        return institutionService.findById(institutionId)
    }

    @Cacheable(
        cacheResolver = CacheConfiguration.APPLICATION_CACHE_KEY,
        keyGenerator = CacheConfiguration.SHA_256_KEY_GEN,
        condition = "!@cacheState.isCacheLocked() && @cacheState.isCacheEnabled()")
    override fun findByLocality(@CacheKey localityId: Long): List<Institution> {
        if (cacheState.isCacheEnabled()) {
            loggerService.info(LogMessage("${this.javaClass.simpleName} caching new value by localityId $localityId"))
        }
        return institutionService.findByLocality(localityId)
    }

}