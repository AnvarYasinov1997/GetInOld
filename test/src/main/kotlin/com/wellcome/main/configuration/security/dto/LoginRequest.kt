package com.wellcome.main.configuration.security.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class LoginRequest @JsonCreator constructor(@param:JsonProperty("username") val username: String,
                                            @param:JsonProperty("password") val password: String)