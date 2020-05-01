package com.wellcome.main.configuration.security.config

import org.springframework.security.authentication.AbstractAuthenticationToken

class SwaggerAuthenticationKey(private val key: String) : AbstractAuthenticationToken(null) {

    override fun getCredentials(): Any? {
        return this.key
    }

    override fun getPrincipal(): Any? {
        return this.key
    }
}