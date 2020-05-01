package com.wellcome.main.service.facade.institution

import com.wellcome.main.entity.institution.PromotedInstitution
import com.wellcome.main.repository.local.postgre.PromotedInstitutionRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

// not implement base service
interface PromotedInstitutionService : BaseService<PromotedInstitution> {
    fun findByInstitutionCategory(institutionCategoryId: Long): List<PromotedInstitution>
}

@Service
open class DefaultPromotedInstitutionService @Autowired constructor(
    private val promotedInstitutionRepository: PromotedInstitutionRepository
) : PromotedInstitutionService,
    DefaultBaseService<PromotedInstitution>(PromotedInstitution::class.java.simpleName, promotedInstitutionRepository) {

    @Transactional(readOnly = true)
    override fun findByInstitutionCategory(institutionCategoryId: Long): List<PromotedInstitution> =
        promotedInstitutionRepository.findByInstitutionCategoryId(institutionCategoryId)

}