package com.wellcome.main.dto.api.newDto.common.v1

data class InstitutionMapDtoV1(val id: Long,
                               val lat: Double,
                               val lon: Double,
                               val address: String,
                               val worksUpDto: WorksUpDtoV1,
                               val phoneList: List<String>,
                               val categoryList: List<CategoryDtoV1>)