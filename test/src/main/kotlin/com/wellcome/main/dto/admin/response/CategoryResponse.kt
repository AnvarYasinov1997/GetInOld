package com.wellcome.main.dto.admin.response

data class CategoryNameResponse(val categoryNameDtos: List<CategoryNameDto>)

data class CategoryNameDto(val id: Long,
                           val name: String)