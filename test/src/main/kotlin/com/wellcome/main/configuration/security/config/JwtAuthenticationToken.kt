package com.wellcome.main.configuration.security.config

import com.wellcome.main.configuration.security.model.ProfileContext
import com.wellcome.main.configuration.security.model.RawAccessJwtToken
import com.wellcome.main.configuration.security.model.UserContext
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class AdminJwtAuthenticationToken : JwtAuthenticationToken {

    constructor(unsafeToken: RawAccessJwtToken) : super(unsafeToken)

    constructor(userContext: UserContext,
                rawAccessJwtToken: RawAccessJwtToken,
                authorities: Collection<GrantedAuthority>) : super(userContext, rawAccessJwtToken, authorities)

}

class ModerationJwtAuthenticationToken : JwtAuthenticationToken {

    constructor(unsafeToken: RawAccessJwtToken) : super(unsafeToken)

    constructor(userContext: UserContext,
                rawAccessJwtToken: RawAccessJwtToken,
                authorities: Collection<GrantedAuthority>) : super(userContext, rawAccessJwtToken, authorities)

}

open class JwtAuthenticationToken : AbstractAuthenticationToken {

    private var rawAccessToken: RawAccessJwtToken? = null

    private var userContext: UserContext? = null

    private var profileContext: ProfileContext? = null

    constructor(unsafeToken: RawAccessJwtToken) : super(null) {
        this.rawAccessToken = unsafeToken
        this.isAuthenticated = false
    }

    constructor(userContext: UserContext,
                rawAccessJwtToken: RawAccessJwtToken,
                authorities: Collection<GrantedAuthority>) : super(authorities) {
        this.eraseCredentials()
        this.rawAccessToken = rawAccessJwtToken
        this.userContext = userContext
        super.setAuthenticated(true)
    }

    constructor(profileContext: ProfileContext,
                rawAccessJwtToken: RawAccessJwtToken,
                authorities: Collection<GrantedAuthority>) : super(authorities) {
        this.eraseCredentials()
        this.rawAccessToken = rawAccessJwtToken
        this.profileContext = profileContext
        super.setAuthenticated(true)
    }

    constructor(rawAccessJwtToken: RawAccessJwtToken,
                authorities: Collection<GrantedAuthority>) : super(authorities) {
        this.eraseCredentials()
        this.rawAccessToken = rawAccessJwtToken
        super.setAuthenticated(true)
    }

    override fun setAuthenticated(authenticated: Boolean) {
        if (authenticated) {
            throw IllegalArgumentException(
                "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead")
        }
        super.setAuthenticated(false)
    }

    override fun getCredentials(): Any? {
        return this.rawAccessToken
    }

    override fun getPrincipal(): Any? {
        return userContext ?: profileContext
    }

}