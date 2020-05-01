package com.wellcome.main.service.management.admin

import com.wellcome.main.dto.admin.response.CategoryNameResponse
import com.wellcome.main.entity.institution.InstitutionCategory
import com.wellcome.main.service.extentions.generators.admin.generateCategoryNameDtoList
import com.wellcome.main.service.facade.institution.InstitutionCategoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface AdminCategoryManagementService {
    fun getCategories(): CategoryNameResponse
}

@Service
open class AdminAdminCategoryManagementService @Autowired constructor(
    private val categoryService: InstitutionCategoryService
) : AdminCategoryManagementService {

    @Transactional(readOnly = true)
    override fun getCategories(): CategoryNameResponse {
        return categoryService.findAll()
            .let(List<InstitutionCategory>::generateCategoryNameDtoList)
            .let(::CategoryNameResponse)
    }

}