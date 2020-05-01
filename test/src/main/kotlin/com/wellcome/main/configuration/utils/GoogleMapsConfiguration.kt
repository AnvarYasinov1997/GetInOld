package com.wellcome.main.configuration.utils

import com.google.maps.GeoApiContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class GoogleMapsConfiguration @Autowired constructor(
    @Value(value = "googleMapsApiKey") private val googleMapsApiKey: String
) {

    @Bean
    open fun initGeoApiContext(): GeoApiContext {
        return GeoApiContext.Builder().apiKey(googleMapsApiKey).build()
    }

}