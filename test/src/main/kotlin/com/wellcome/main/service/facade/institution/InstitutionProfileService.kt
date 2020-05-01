package com.wellcome.main.service.facade.institution

import com.wellcome.main.entity.institutionProfile.InstitutionProfile
import com.wellcome.main.repository.local.postgre.InstitutionProfileRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityNotFoundException

interface InstitutionProfileService : BaseService<InstitutionProfile> {
    fun findByLogin(login: String): InstitutionProfile
    fun findByInstitution(institutionId: Long): InstitutionProfile?
}

@Service
open class DefaultInstitutionProfileService @Autowired constructor(
    private val institutionProfileRepository: InstitutionProfileRepository
) : InstitutionProfileService,
    DefaultBaseService<InstitutionProfile>(InstitutionProfile::class.java.simpleName, institutionProfileRepository) {

    @Transactional(readOnly = true)
    override fun findByLogin(login: String): InstitutionProfile =
        institutionProfileRepository.findByLogin(login).orElseThrow {
            EntityNotFoundException("InstitutionProfile width login: $login is not fount to database")
        }

    @Transactional(readOnly = true)
    override fun findByInstitution(institutionId: Long): InstitutionProfile? =
        institutionProfileRepository.findByInstitutionId(institutionId).orElseGet { null }

}