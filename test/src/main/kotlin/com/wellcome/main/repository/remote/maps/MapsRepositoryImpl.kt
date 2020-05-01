package com.wellcome.main.repository.remote.maps

import com.google.maps.GeoApiContext
import com.wellcome.main.model.*
import com.wellcome.main.service.utils.LogMessage
import com.wellcome.main.service.utils.LoggerService
import com.wellcome.main.util.functions.getAddress
import com.wellcome.main.util.functions.getLocality
import com.wellcome.main.util.functions.getTimezoneId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

interface MapsRepository {
    fun findLocality(lat: Double, lon: Double): MapsReturn
    fun findAddress(lat: Double, lon: Double): MapsReturn
}

@Repository
open class DefaultMapsRepository @Autowired constructor(
    private val logger: LoggerService,
    private val geoApiContext: GeoApiContext
) : MapsRepository {

    override fun findLocality(lat: Double, lon: Double): MapsReturn {
        val localityName = geoApiContext.getLocality(lat, lon)
        val timezoneId = geoApiContext.getTimezoneId(lat, lon)

        logger.info(LogMessage("locality $localityName timezone $timezoneId geopoints $lat $lon"))

        return if (localityName.isEmpty() || timezoneId.isEmpty())
            LocalityNotFound("locality $localityName timezone $timezoneId")
        else LocalityFound(localityName, timezoneId)
    }

    override fun findAddress(lat: Double, lon: Double): MapsReturn {
        val address = geoApiContext.getAddress(lat, lon)

        logger.info(LogMessage("address $address"))

        return if (address.isEmpty())
            AddressNotFound("address $address")
        else AddressFound(address)
    }

}