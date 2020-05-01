package com.wellcome.main.service.extentions.generators.api.common

import com.wellcome.main.dto.api.newDto.common.v1.CategoryDtoV1
import com.wellcome.main.entity.institution.InstitutionCategory

fun List<InstitutionCategory>.generateCategoryDtoV1List(): List<CategoryDtoV1> =
    this.map { CategoryDtoV1(it.title, it.categoryType.name, requireNotNull(it.id)) }