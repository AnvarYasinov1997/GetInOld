package com.wellcome.main.configuration.security.util

object SecurityPaths {

    private const val API = "/api/new"

    private const val ADMIN = "/admin"

    private const val MODERATION = "/moderation"

    private const val APPLICATION_VERSION = "$API/v1"

    const val ADMIN_JWT_TOKEN_HEADER_PARAM = "jwt-token"

    const val MODERATION_JWT_TOKEN_HEADER_PARAM = "moderation-jwt-token"

    const val GOOGLE_TOKEN_HEADER_PARAM = "token"

    const val FORM_BASED_LOGIN_ENTRY_POINT = "$ADMIN/auth"

    const val ADMIN_TOKEN_BASED_AUTH_ENTRY_POINT = "$ADMIN/**"

    const val MODERATION_TOKEN_BASED_AUTH_ENTRY_POINT = "$MODERATION/**"

    const val GOOGLE_TOKEN_BASED_AUTH_ENTRY_POINT = "$API/**"

    const val RESET_PASSWORD_ENTRY_POINT = "$ADMIN/passwords/**"

    const val TOKEN_REFRESH_ENTRY_POINT = "$ADMIN/auth/token"

    const val USER_INIT_ENTRY_POINT = "$APPLICATION_VERSION/auth/initUser"

}