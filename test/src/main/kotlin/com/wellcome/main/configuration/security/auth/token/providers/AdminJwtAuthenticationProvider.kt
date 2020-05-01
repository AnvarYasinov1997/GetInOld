package com.wellcome.main.configuration.security.auth.token.providers

import com.wellcome.main.configuration.security.config.AdminJwtAuthenticationToken
import com.wellcome.main.configuration.security.config.JwtAuthenticationToken
import com.wellcome.main.configuration.security.model.RawAccessJwtToken
import com.wellcome.main.configuration.security.model.UserContext
import com.wellcome.main.configuration.security.property.AuthenticationJwtProperty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component

@Component(value = "admin-jwt")
open class AdminJwtAuthenticationProvider @Autowired constructor(
    private val authenticationJwtProperty: AuthenticationJwtProperty
) : AuthenticationProvider {

    @Throws(AuthenticationException::class)
    override fun authenticate(authentication: Authentication): Authentication {
        val rawAccessJwtToken = authentication.credentials as RawAccessJwtToken

        val jwsClaims = rawAccessJwtToken.parseClaims(authenticationJwtProperty.tokenSigningKey)

        val id = jwsClaims.body.id.toLong()

        val username = jwsClaims.body.subject

        val scopes = jwsClaims.body.get("scopes", List::class.java)

        val authorities: List<GrantedAuthority> = scopes.map { SimpleGrantedAuthority(it.toString()) }

        val userContext = UserContext.create(id, rawAccessJwtToken.getToken(), username, null, authorities)

        return JwtAuthenticationToken(userContext, rawAccessJwtToken, userContext.authorities)
    }

    override fun supports(authentication: Class<*>): Boolean {
        return AdminJwtAuthenticationToken::class.java.isAssignableFrom(authentication)
    }

}
