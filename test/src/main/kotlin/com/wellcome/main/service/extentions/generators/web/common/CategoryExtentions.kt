package com.wellcome.main.service.extentions.generators.web.common

import com.wellcome.main.dto.web.common.CategoryDto
import com.wellcome.main.entity.institution.InstitutionCategory

fun List<InstitutionCategory>.generateCategoryDtoList(): List<CategoryDto> =
    this.map { CategoryDto(it.title, it.categoryType.name, requireNotNull(it.id)) }