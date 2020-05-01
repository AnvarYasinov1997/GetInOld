package com.wellcome.main.configuration.security.model

enum class Scopes {

    REFRESH_TOKEN;

    fun authority(): String {
        return "ROLE_" + this.name
    }

}