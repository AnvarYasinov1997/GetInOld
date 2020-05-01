package com.wellcome.main.configuration.utils

import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class TranslationConfiguration {

    @Bean
    open fun getTranslate(): Translate {
        return TranslateOptions.getDefaultInstance().service
    }

}