package com.wellcome.main.configuration.utils

import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class ApplicationContextProviderConfiguration {

    @Bean
    open fun applicationContextProvider(applicationContext: ApplicationContext): ApplicationContextProvider {
        return ApplicationContextProvider(applicationContext)
    }

}

class ApplicationContextProvider(context: ApplicationContext) {

    init {
        applicationContext = context
    }

    companion object {
        private var applicationContext: ApplicationContext? = null
        fun getApplicationContext(): ApplicationContext {
            if (applicationContext != null) {
                return requireNotNull(applicationContext)
            } else throw NullPointerException("Field application context has not be initialized")
        }
    }

}