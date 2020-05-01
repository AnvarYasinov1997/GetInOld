package com.wellcome.main.configuration.security.config

import com.wellcome.main.configuration.security.model.GoogleAccessToken
import com.wellcome.main.configuration.security.model.UserContext
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class GoogleAuthenticationToken : AbstractAuthenticationToken {

    private var googleAccessToken: GoogleAccessToken? = null

    private var userContext: UserContext? = null

    constructor(unsafeToken: GoogleAccessToken) : super(null) {
        this.googleAccessToken = unsafeToken
        this.isAuthenticated = false
    }

    constructor(userContext: UserContext, authorities: Collection<GrantedAuthority>) : super(authorities) {
        this.eraseCredentials()
        this.userContext = userContext
        super.setAuthenticated(true)
    }

    override fun setAuthenticated(authenticated: Boolean) {
        if (authenticated) {
            throw IllegalArgumentException(
                "Cannot set this token to trusted -" +
                    " use constructor which takes a GrantedAuthority list instead")
        }
        super.setAuthenticated(false)
    }

    override fun getCredentials(): Any? {
        return this.googleAccessToken
    }

    override fun getPrincipal(): Any? {
        return this.userContext
    }

    override fun eraseCredentials() {
        super.eraseCredentials()
        this.googleAccessToken = null
    }

}