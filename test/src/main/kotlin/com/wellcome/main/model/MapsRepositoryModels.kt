package com.wellcome.main.model

sealed class MapsReturn
data class LocalityFound(val locality: String,
                         val timezoneId: String) : MapsReturn()

data class LocalityNotFound(val reason: String) : MapsReturn()

data class AddressFound(val street: String) : MapsReturn()

data class AddressNotFound(val reason: String) : MapsReturn()