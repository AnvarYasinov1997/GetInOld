package com.wellcome.main.util.functions

import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.TimeZoneApi
import com.google.maps.model.AddressComponentType
import com.google.maps.model.AddressType
import com.google.maps.model.LatLng

fun GeoApiContext.getLocality(lat: Double, lon: Double): String {
    val results = GeocodingApi
        .reverseGeocode(this@getLocality, LatLng(lat, lon))
        .resultType(AddressType.LOCALITY)
        .await()
    results.forEach { res ->
        res.addressComponents.forEach { addressComponent ->
            addressComponent.types.forEach { type ->
                if (type == AddressComponentType.LOCALITY) {
                    return addressComponent.longName
                }
            }
        }
    }
    return ""
}

fun GeoApiContext.getTimezoneId(lat: Double, lon: Double): String {
    return try {
        val result = TimeZoneApi.getTimeZone(this@getTimezoneId, LatLng(lat, lon)).await()
        when {
            result == null -> ""
            result.id == null -> ""
            else -> result.id
        }
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

fun GeoApiContext.getAddress(lat: Double, lon: Double): String {
    val result = GeocodingApi
        .reverseGeocode(this@getAddress, LatLng(lat, lon))
        .await()
    if (result.isNotEmpty()) {
        return result[0].formattedAddress
    }
    return ""
}