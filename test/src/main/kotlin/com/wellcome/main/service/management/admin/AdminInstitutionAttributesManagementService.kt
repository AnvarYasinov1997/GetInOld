package com.wellcome.main.service.management.admin

import com.wellcome.main.dto.admin.response.InstitutionAttributesResponse
import com.wellcome.main.service.extentions.generators.admin.generateCategoryDtoList
import com.wellcome.main.service.extentions.generators.admin.generateTagDtoList
import com.wellcome.main.service.facade.institution.InstitutionCategoryService
import com.wellcome.main.service.facade.institution.InstitutionTagService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface AdminInstitutionAttributesManagementService {
    fun getAttributes(): InstitutionAttributesResponse
}

@Service
open class DefaultAdminInstitutionAttributesManagementService @Autowired constructor(
    private val institutionTagService: InstitutionTagService,
    private val institutionCategoryService: InstitutionCategoryService
) : AdminInstitutionAttributesManagementService {

    @Transactional(readOnly = true)
    override fun getAttributes(): InstitutionAttributesResponse {
        val tags = institutionTagService.findAll()
        val categories = institutionCategoryService.findAll()
        return InstitutionAttributesResponse(tags.generateTagDtoList(), categories.generateCategoryDtoList())
    }

}