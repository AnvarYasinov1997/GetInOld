package com.wellcome.main.dto.web.common

data class BirthdayCampaignDto(val id: Long,
                               val age: String,
                               val text: String,
                               val gender: String,
                               val status: String,
                               val creationDate: String,
                               val expirationDate: String,
                               val institutionDto: InstitutionDto)