package com.wellcome.main.configuration.utils

import com.wellcome.main.databaseUtils.CustomSequenceGenerator
import com.wellcome.main.databaseUtils.SequenceCacheUploader
import com.wellcome.main.service.facade.baseService.BaseServiceSerializable
import com.wellcome.main.service.utils.LoggerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SequenceCacheConfiguration @Autowired constructor(
    private val serviceSerializables: List<BaseServiceSerializable>
) {

    @Bean
    open fun sequenceCacheUploader(loggerService: LoggerService): SequenceCacheUploader {
        return CustomSequenceGenerator.sequenceCache.also {
            it.loggerService = loggerService
            it.serviceSerializables = this.serviceSerializables
        }
    }

}