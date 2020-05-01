package com.wellcome.main.configuration.security.dto

import com.wellcome.main.dto.api.newDto.common.v1.InstitutionDtoV1

data class AuthorizeInstitutionResponseV1(val institutionDto: InstitutionDtoV1,
                                          val token: String)