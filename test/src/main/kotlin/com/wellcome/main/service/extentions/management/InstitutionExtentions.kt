package com.wellcome.main.service.extentions.management

import com.wellcome.main.entity.institution.*


fun List<Institution>.filterByInstitutionCategory(categoryType: InstitutionCategoryType): List<Institution> =
    this.filter {
        it.categories.map(InstitutionCategory::categoryType).contains(categoryType)
    }

fun List<Institution>.filterNotByInstitutionCategory(categoryType: InstitutionCategoryType): List<Institution> =
    this.filterNot {
        it.categories.map(InstitutionCategory::categoryType).contains(categoryType)
    }

fun List<Institution>.sortByName(): List<Institution> =
    this.sortedBy { it.name }