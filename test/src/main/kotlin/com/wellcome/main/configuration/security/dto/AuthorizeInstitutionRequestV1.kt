package com.wellcome.main.configuration.security.dto

data class AuthorizeInstitutionRequestV1(val login: String,
                                         val password: String)