package com.wellcome.main.configuration.utils

import com.getin.main.applicationContextConfiguration.ReloadCacheAnnotationBeanPostProcessor
import com.wellcome.main.component.CacheReloader
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.Executor

@Configuration
open class BeanPostProcessorConfiguration {

    @Bean
    open fun adminInstitutionManagementServiceAddMethodBeanPostProcessor(threadCache: ThreadCache,
                                                                         cacheReloader: CacheReloader,
                                                                         @Qualifier("cacheReloaderExecutor") executor: Executor): ReloadCacheAnnotationBeanPostProcessor {
        return ReloadCacheAnnotationBeanPostProcessor(threadCache, cacheReloader, executor)
    }

}