package com.wellcome.main.dto.api.newDto.common.v1

data class BirthdayCampaignDtoV1(val id: Long,
                                 val age: String,
                                 val text: String,
                                 val gender: String,
                                 val status: String,
                                 val creationDate: String,
                                 val expirationDate: String,
                                 val institutionDto: InstitutionDtoV1)