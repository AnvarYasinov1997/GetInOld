package com.wellcome.main.component

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

interface SerializationProvider {
    fun <K, V> serializeJsonStringToMap(json: String): Map<K, V>
    fun <K, V> serializeJsonStringToListOfMap(json: String): List<Map<K, V>>
}

@Component
open class DefaultSerializationProvider @Autowired constructor(
    private val objectMapper: ObjectMapper
) : SerializationProvider {

    override fun <K, V> serializeJsonStringToMap(json: String): Map<K, V> {
        val typeReference = object : TypeReference<Map<K, V>>() {}
        return objectMapper.readValue(json, typeReference)
    }

    override fun <K, V> serializeJsonStringToListOfMap(json: String): List<Map<K, V>> {
        val typeReference = object : TypeReference<List<Map<K, V>>>() {}
        return objectMapper.readValue(json, typeReference)
    }

}