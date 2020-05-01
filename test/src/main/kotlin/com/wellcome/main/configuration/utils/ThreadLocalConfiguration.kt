package com.wellcome.main.configuration.utils

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.ZonedDateTime

interface ThreadCache {
    fun getLocalityIdRequestThreadLocal(): ThreadLocal<Long>
    fun getUserZonedDateTimeRequestThreadLocal(): ThreadLocal<ZonedDateTime>
}

@Configuration
open class ThreadLocalConfiguration {

    @Bean
    open fun getThreadCache(): ThreadCache {
        return userZonedDateTimeRequestThreadLocal
    }

    companion object {
        val userZonedDateTimeRequestThreadLocal: ThreadCache = ThreadCacheImpl(ThreadLocal(), ThreadLocal())
    }

    private class ThreadCacheImpl(
        private val localityIdRequestThreadLocal: ThreadLocal<Long>,
        private val userZonedDateTimeRequestThreadLocal: ThreadLocal<ZonedDateTime>
    ) : ThreadCache {

        override fun getLocalityIdRequestThreadLocal(): ThreadLocal<Long> =
            this.localityIdRequestThreadLocal

        override fun getUserZonedDateTimeRequestThreadLocal(): ThreadLocal<ZonedDateTime> =
            this.userZonedDateTimeRequestThreadLocal

    }

}