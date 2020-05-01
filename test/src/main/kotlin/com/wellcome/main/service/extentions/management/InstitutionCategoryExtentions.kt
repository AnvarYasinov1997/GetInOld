package com.wellcome.main.service.extentions.management

import com.wellcome.main.entity.institution.InstitutionCategory
import com.wellcome.main.entity.institution.InstitutionCategoryType

fun List<InstitutionCategory>.sortCategories(): List<InstitutionCategory> =
    InstitutionCategoryType.getRangingSequence().mapNotNull { categoryType ->
        this.firstOrNull { it.categoryType == categoryType }
    }