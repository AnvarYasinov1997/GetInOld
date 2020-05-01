package com.wellcome.main.configuration.security.auth.token.providers

import com.wellcome.main.configuration.security.config.GoogleAuthenticationToken
import com.wellcome.main.configuration.security.model.GoogleAccessToken
import com.wellcome.main.configuration.security.model.UserContext
import com.wellcome.main.configuration.security.service.AuthenticationManagementService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.stereotype.Component

@Component(value = "google")
open class GoogleAuthenticationProvider @Autowired constructor(
    private val authenticationManagementService: AuthenticationManagementService
) : AuthenticationProvider {

    @Throws(AuthenticationException::class)
    override fun authenticate(authentication: Authentication): Authentication {
        val googleToken = authentication.credentials as GoogleAccessToken

        val result = try {
            authenticationManagementService.verify(googleToken.getToken())
        } catch (ex: Exception) {
            authenticationManagementService.initUser(googleToken.getToken())
        }

        if (result.isBlocked)
            throw InsufficientAuthenticationException("User is blocked")

        val userContext =
            UserContext.create(result.userId, "", result.email, result.localityId, result.authority)

        return GoogleAuthenticationToken(userContext, userContext.authorities)
    }

    override fun supports(authentication: Class<*>): Boolean {
        return GoogleAuthenticationToken::class.java.isAssignableFrom(authentication)
    }

}