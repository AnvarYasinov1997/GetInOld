package com.wellcome.main.configuration.security.util.token

import com.wellcome.main.configuration.security.model.*
import com.wellcome.main.configuration.security.property.AuthenticationJwtProperty
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.ZonedDateTime
import java.util.*

interface TokenFactory {
    fun createAccessJwtToken(userContext: UserContext): Token
    fun createRefreshToken(userContext: UserContext): Token
    fun createCustomerAccessToken(userName: String, institutionProfiles: List<ProfileModel>): Token
}

@Component
open class JwtTokenFactory @Autowired constructor(
    private val authenticationJwtProperty: AuthenticationJwtProperty
) : TokenFactory {

    override fun createCustomerAccessToken(userName: String, institutionProfiles: List<ProfileModel>): Token {
        val claims = Jwts.claims().setSubject(userName).also { claims ->
            claims.issuer = authenticationJwtProperty.tokenIssuer
            claims["profiles"] = institutionProfiles.map {
                mapOf(
                    "institutionProfileId" to it.institutionProfileId,
                    "institutionId" to it.institutionId,
                    "accessKey" to it.accessKey,
                    "login" to it.login,
                    "authorities" to listOf<String>()
                )
            }
        }

        val currentTime = ZonedDateTime.now()

        val expirationTime = authenticationJwtProperty.tokenExpirationTime

        val token = Jwts.builder()
            .setClaims(claims)
            .setIssuer(authenticationJwtProperty.tokenIssuer)
            .setIssuedAt(Date(currentTime.toInstant().toEpochMilli()))
            .setExpiration(Date(currentTime.plusMinutes(expirationTime).toInstant().toEpochMilli()))
            .signWith(SignatureAlgorithm.HS512, authenticationJwtProperty.tokenSigningKey)
            .compact()

        return AccessJwtToken(token, claims)
    }

    override fun createAccessJwtToken(userContext: UserContext): Token {
        val claims = Jwts.claims().setSubject(userContext.username)

        claims.id = userContext.id.toString()

        claims.issuer = authenticationJwtProperty.tokenIssuer

        claims["scopes"] = userContext.authorities.map { it.authority }.toList()

        val currentTime = ZonedDateTime.now()

        val expirationTime = authenticationJwtProperty.tokenExpirationTime

        val token = Jwts.builder()
            .setClaims(claims)
            .setIssuer(authenticationJwtProperty.tokenIssuer)
            .setIssuedAt(Date(currentTime.toInstant().toEpochMilli()))
            .setExpiration(Date(currentTime.plusMinutes(expirationTime).toInstant().toEpochMilli()))
            .signWith(SignatureAlgorithm.HS512, authenticationJwtProperty.tokenSigningKey)
            .compact()

        return AccessJwtToken(token, claims)
    }

    override fun createRefreshToken(userContext: UserContext): Token {
        val currentTime = ZonedDateTime.now()

        val claims = Jwts.claims().setSubject(userContext.username)

        claims.id = userContext.id.toString()

        claims.issuer = authenticationJwtProperty.tokenIssuer

        claims["scopes"] = listOf(Scopes.REFRESH_TOKEN.authority())

        val expirationTime = authenticationJwtProperty.refreshTokenExpTime

        val token = Jwts.builder()
            .setClaims(claims)
            .setIssuer(authenticationJwtProperty.tokenIssuer)
            .setId(UUID.randomUUID().toString())
            .setIssuedAt(Date(currentTime.toInstant().toEpochMilli()))
            .setExpiration(Date(currentTime.plusMinutes(expirationTime).toInstant().toEpochMilli()))
            .signWith(SignatureAlgorithm.HS512, authenticationJwtProperty.tokenSigningKey)
            .compact()

        return AccessJwtToken(token, claims)
    }

}
