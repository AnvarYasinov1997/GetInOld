package com.wellcome.main.configuration.utils

import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.bigquery.BigQuery
import com.google.cloud.bigquery.BigQueryOptions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource

@Configuration
open class BigQueryConfiguration @Autowired constructor(
    @Value(value = "classpath:Get-in-big-query.json") private val resource: Resource
){

    @Bean
    open fun bigQuery(): BigQuery {
        return BigQueryOptions.newBuilder().setCredentials(
            ServiceAccountCredentials.fromStream(resource.inputStream)
        ).build().service
    }

}