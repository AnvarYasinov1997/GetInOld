package com.wellcome.main.configuration.security.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Component
@Configuration
@ConfigurationProperties(prefix = "auth-jwt-token")
open class AuthenticationJwtProperty(var tokenSigningKey: String = "tokenCryptoHashKey",
                                     var tokenExpirationTime: Long = 10000000,
                                     var tokenIssuer: String = "getInApp",
                                     var refreshTokenExpTime: Long = 1000000000000)