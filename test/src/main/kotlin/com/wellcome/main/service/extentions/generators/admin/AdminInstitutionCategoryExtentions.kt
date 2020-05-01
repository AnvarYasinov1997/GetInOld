package com.wellcome.main.service.extentions.generators.admin

import com.wellcome.main.dto.admin.common.CategoryDto
import com.wellcome.main.dto.admin.response.CategoryNameDto
import com.wellcome.main.entity.institution.InstitutionCategory

fun List<InstitutionCategory>.generateCategoryDtoList(): List<CategoryDto> = this.map {
    CategoryDto(
        id = requireNotNull(it.id),
        name = it.categoryType.name,
        checked = false
    )
}

fun List<InstitutionCategory>.generateCategoryNameDtoList(): List<CategoryNameDto> = this.map {
    CategoryNameDto(
        id = requireNotNull(it.id),
        name = it.categoryType.name
    )
}