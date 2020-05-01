package com.wellcome.main.configuration.utils

import com.wellcome.main.service.utils.LogMessage
import com.wellcome.main.service.utils.LoggerService
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.lang.reflect.Method
import java.util.concurrent.Executor

@Configuration
open class AsyncThreadPoolConfiguration @Autowired constructor(
    private val loggerService: LoggerService
) : AsyncConfigurer, AsyncUncaughtExceptionHandler {

    @Bean
    @Qualifier(value = "managementService")
    open fun managementServiceExecutor(): Executor =
        ThreadPoolTaskExecutor().also {
            it.maxPoolSize = 100
            it.corePoolSize = 10
            it.threadNamePrefix = "Get-in async pool"
        }

    @Bean
    @Qualifier(value = "cacheReloaderExecutor")
    open fun cacheReloaderExecutor(): Executor =
        ThreadPoolTaskExecutor().also {
            it.maxPoolSize = 100
            it.corePoolSize = 10
            it.threadNamePrefix = "Get-in cache reloader singleton pool"
        }

    @Bean
    override fun getAsyncExecutor(): Executor? = ThreadPoolTaskExecutor().also {
        it.maxPoolSize = 10
        it.threadNamePrefix = "Get-in async pool"
    }

    override fun handleUncaughtException(ex: Throwable, method: Method, vararg params: Any?) {
        loggerService.error(LogMessage(ex.toString()))
    }

    override fun getAsyncUncaughtExceptionHandler(): AsyncUncaughtExceptionHandler? {
        return this
    }

}