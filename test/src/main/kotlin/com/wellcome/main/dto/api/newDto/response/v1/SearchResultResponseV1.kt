package com.wellcome.main.dto.api.newDto.response.v1

import com.wellcome.main.dto.api.newDto.common.v1.InstitutionDtoV1

data class SearchResultResponseV1(val institutionDtos: List<InstitutionDtoV1>)