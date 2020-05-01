package com.wellcome.main.dto.admin.response

data class LocalityResponse(val localityDto: List<LocalityDto>)

data class LocalityDto(val id: Long,
                       val name: String)