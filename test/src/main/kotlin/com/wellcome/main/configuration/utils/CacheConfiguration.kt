package com.wellcome.main.configuration.utils

import com.wellcome.main.annotations.CacheKey
import com.wellcome.main.component.CryptographyProvider
import com.wellcome.main.entity.Locality
import com.wellcome.main.service.facade.LocalityService
import com.wellcome.main.service.utils.LogMessage
import com.wellcome.main.service.utils.LoggerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.cache.interceptor.CacheOperationInvocationContext
import org.springframework.cache.interceptor.CacheResolver
import org.springframework.cache.interceptor.KeyGenerator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.lang.reflect.Method
import java.time.ZonedDateTime
import java.util.concurrent.atomic.AtomicBoolean

interface CustomCacheManager {
    fun getCache(key: String): Cache
    fun getValueFromCache(cacheKey: String, entryKey: String): Any?
    fun putValueInCache(cacheKey: String, entryKey: String, value: Any)
    fun evictCache(cacheKey: String, entryKey: String)
    fun clearCache(cacheKey: String)
    fun clearAllCaches()
}

interface CacheState {
    fun enableCache()
    fun disableCache()
    fun isCacheEnabled(): Boolean
    fun lockCache()
    fun unlockCache()
    fun isCacheLocked(): Boolean
}

@Configuration
open class CacheConfiguration @Autowired constructor(
    private val threadCache: ThreadCache,
    private val loggerService: LoggerService,
    private val localityService: LocalityService,
    private val cryptographyProvider: CryptographyProvider
) {

    @Bean
    @Qualifier("cacheState")
    open fun cacheState(): CacheState {
        return DefaultCacheState(loggerService)
    }

    @Bean
    open fun customCacheManager(cacheManager: CacheManager): CustomCacheManager {
        return DefaultCustomCacheManager(cacheManager)
    }

    @Bean
    @Qualifier(value = SHA_256_KEY_GEN)
    open fun keyGenerator(): KeyGenerator {
        return CustomKeyGenerator(cryptographyProvider)
    }

    @Bean
    @Qualifier(value = APPLICATION_CACHE_KEY)
    open fun cacheManager(): CacheManager {
        val cacheNames = localityService.findAll().map(Locality::name).toMutableList().also {
            it.add("default")
        }.toTypedArray()
        return ConcurrentMapCacheManager(*cacheNames)
    }

    @Bean
    @Qualifier(value = APPLICATION_CACHE_KEY)
    open fun cacheKeys(): List<Pair<Long, String>> {
        return localityService.findAll().map { Pair(requireNotNull(it.id), it.name) }
    }

    @Bean
    @Qualifier(value = APPLICATION_CACHE_KEY)
    open fun cacheResolver(@Qualifier(value = APPLICATION_CACHE_KEY) cacheManager: CacheManager,
                           @Qualifier(value = APPLICATION_CACHE_KEY) cacheKeys: List<Pair<Long, String>>): CacheResolver {
        return ApplicationCacheResolver(threadCache, cacheManager, loggerService, cacheKeys)
    }

    private class CustomKeyGenerator(private val cryptographyProvider: CryptographyProvider) : KeyGenerator {
        override fun generate(target: Any, method: Method, vararg params: Any): Any {
            var keyArgNumber = 0
            for (i in method.parameters) {
                if (i.isAnnotationPresent(CacheKey::class.java)) continue
                keyArgNumber++
            }
            return StringBuilder()
                .append(target::class.java.simpleName)
                .append("_")
                .append(method.name)
                .append("_")
                .append(params[keyArgNumber].toString())
                .toString()
        }
    }

    private class ApplicationCacheResolver(private val threadCache: ThreadCache,
                                           private val cacheManager: CacheManager,
                                           private val loggerService: LoggerService,
                                           private val cacheKeys: List<Pair<Long, String>>) : CacheResolver {

        override fun resolveCaches(context: CacheOperationInvocationContext<*>): MutableCollection<out Cache> {
            val localityId = threadCache.getLocalityIdRequestThreadLocal().get()
            if (localityId == null) {
                StringBuilder()
                    .append(ZonedDateTime.now())
                    .append("  ")
                    .append("[ERROR]")
                    .append("  ")
                    .append("Method - ")
                    .append("\"${context.method.name}\"")
                    .append(" in object - ")
                    .append(context.target.toString())
                    .append(" does not work correctly.")
                    .let(StringBuilder::toString)
                    .let(::LogMessage)
                    .let(loggerService::sendLogDeveloper)
                return requireNotNull(cacheManager.getCache(ERROR_CACHE_KEY))
                    .let(::listOf)
                    .toMutableList()
            }
            val cacheName = cacheKeys.first { it.first == requireNotNull(localityId) }
            return requireNotNull(cacheManager.getCache(cacheName.second))
                .let(::listOf)
                .toMutableList()
        }

        companion object {
            private const val ERROR_CACHE_KEY = "default"
        }

    }

    private class DefaultCacheState(private val loggerService: LoggerService) : CacheState {

        private val cacheMutex: AtomicBoolean = AtomicBoolean(false)

        private val cacheEnabled: AtomicBoolean = AtomicBoolean(true)

        override fun enableCache() {
            cacheEnabled.set(true)
            val message = LogMessage("Cache enabled")
            loggerService.info(message)
            loggerService.sendLogDeveloper(message)
        }

        override fun disableCache() {
            cacheEnabled.set(false)
            val message = LogMessage("Cache disabled")
            loggerService.info(message)
            loggerService.sendLogDeveloper(message)
        }

        override fun isCacheEnabled(): Boolean {
            return cacheEnabled.get()
        }

        override fun lockCache() {
            cacheMutex.set(true)
        }

        override fun unlockCache() {
            cacheMutex.set(false)
        }

        override fun isCacheLocked(): Boolean {
            return cacheMutex.get()
        }

    }

    private class DefaultCustomCacheManager(private val cacheManager: CacheManager) : CustomCacheManager {

        override fun getCache(key: String): Cache {
            return cacheManager.getCache(key).let(::requireNotNull)
        }

        override fun getValueFromCache(cacheKey: String, entryKey: String): Any? {
            return cacheManager.getCache(cacheKey).let(::requireNotNull).get(entryKey)?.get()
        }

        override fun putValueInCache(cacheKey: String, entryKey: String, value: Any) {
            cacheManager.getCache(cacheKey).let(::requireNotNull).put(entryKey, value)
        }

        override fun evictCache(cacheKey: String, entryKey: String) {
            cacheManager.getCache(cacheKey).let(::requireNotNull).evict(entryKey)
        }

        override fun clearAllCaches() {
            cacheManager.cacheNames.forEach {
                cacheManager.getCache(it).let(::requireNotNull).clear()
            }
        }

        override fun clearCache(cacheKey: String) {
            cacheManager.getCache(cacheKey).let(::requireNotNull).clear()
        }

    }

    companion object {
        const val SHA_256_KEY_GEN = "SHA256KeyGenerator"
        const val APPLICATION_CACHE_KEY = "application"
    }

}