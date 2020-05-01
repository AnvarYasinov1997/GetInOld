package com.wellcome.main.configuration.security.model

import com.wellcome.main.exception.JwtExpiredTokenException
import io.jsonwebtoken.*
import org.springframework.security.authentication.BadCredentialsException

interface Token {
    fun getToken(): String
}

class RawAccessJwtToken(private val token: String) : Token {

    fun parseClaims(signingKey: String): Jws<Claims> {
        var result: Jws<Claims>? = null
        try {
            result = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token)
        } catch (e: Exception) {
            when (e) {
                is UnsupportedJwtException -> throw BadCredentialsException("Invalid JWT token: ", e)
                is MalformedJwtException -> throw BadCredentialsException("Invalid JWT token: ", e)
                is IllegalArgumentException -> throw BadCredentialsException("Invalid JWT token: ", e)
                is SignatureException -> throw BadCredentialsException("Invalid JWT token: ", e)
                is ExpiredJwtException -> throw JwtExpiredTokenException("JWT Token expired", e)
            }
        }
        return result
    }

    override fun getToken(): String = token
}

class AccessJwtToken(private val rawToken: String,
                     private val claims: Claims) : Token {
    override fun getToken(): String = rawToken
}

class GoogleAccessToken(private val token: String) : Token {
    override fun getToken(): String = token
}
