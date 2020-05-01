package com.wellcome.main.service.facade.institution

import com.wellcome.main.entity.institution.InstitutionCategory
import com.wellcome.main.entity.institution.InstitutionCategoryType
import com.wellcome.main.repository.local.postgre.InstitutionCategoryRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface InstitutionCategoryService : BaseService<InstitutionCategory> {
    fun findRanging(): List<InstitutionCategory>
    fun findByCategoryTypeOrNull(institutionCategoryType: InstitutionCategoryType): InstitutionCategory?
}

@Service
open class DefaultInstitutionCategoryService @Autowired constructor(
    private val institutionCategoryRepository: InstitutionCategoryRepository
) : InstitutionCategoryService,
    DefaultBaseService<InstitutionCategory>(InstitutionCategory::class.java.simpleName, institutionCategoryRepository) {

    @Transactional(readOnly = true)
    override fun findRanging(): List<InstitutionCategory> =
        institutionCategoryRepository.findByRangingTrue()

    @Transactional(readOnly = true)
    override fun findByCategoryTypeOrNull(institutionCategoryType: InstitutionCategoryType): InstitutionCategory? =
        institutionCategoryRepository.findByCategoryType(institutionCategoryType).orElseGet { null }

}