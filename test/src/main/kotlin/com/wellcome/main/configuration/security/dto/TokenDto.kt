package com.wellcome.main.configuration.security.dto

data class PairTokenDto(val token: String,
                        val refreshToken: String)

data class TokenDto(val token: String)
