package com.wellcome.main.configuration.security.model

import org.springframework.security.core.GrantedAuthority

class UserContext(val id: Long,
                  val username: String,
                  val localityId: Long?,
                  val token: String,
                  val authorities: Collection<GrantedAuthority>) {

    companion object {
        fun create(id: Long,
                   token: String,
                   username: String,
                   localityId: Long?,
                   authorities: Collection<GrantedAuthority>): UserContext {
            return UserContext(id, username, localityId, token, authorities)
        }
    }
}