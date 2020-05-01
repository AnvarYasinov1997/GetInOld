package com.wellcome.main.service.facade.institution

import com.wellcome.main.entity.institution.Institution
import com.wellcome.main.repository.local.postgre.InstitutionRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityNotFoundException

interface InstitutionService : BaseService<Institution> {
    fun findByInstagramAccount(instagramAccount: String): Institution
    fun findIntactByLocality(localityId: Long): List<Institution>
    fun findByLocality(localityId: Long): List<Institution>
    fun findByLocalityForEvents(localityId: Long): List<Institution>
    fun findRangingByLocality(localityId: Long): List<Institution>
    fun findByLocalityForAdmin(localityId: Long): List<Institution>
    fun findProcessingInstitutions(): List<Institution>
}

@Service
open class DefaultInstitutionService @Autowired constructor(
    private val institutionRepository: InstitutionRepository
) : InstitutionService,
    DefaultBaseService<Institution>(Institution::class.java.simpleName, institutionRepository) {

    @Transactional(readOnly = true)
    override fun findByInstagramAccount(instagramAccount: String): Institution =
        institutionRepository.findByInstagramAccount(instagramAccount).orElseThrow {
            EntityNotFoundException("Institution with instagram account: $instagramAccount is not found to database")
        }

    @Transactional(readOnly = true)
    override fun findByLocalityForAdmin(localityId: Long): List<Institution> =
        institutionRepository.findByLocalityId(localityId)

    @Transactional(readOnly = true)
    override fun findIntactByLocality(localityId: Long): List<Institution> =
        institutionRepository.findByLocalityIdAndBlockedFalseAndProcessingFalse(localityId)

    @Transactional
    override fun findRangingByLocality(localityId: Long): List<Institution> =
        institutionRepository.findByLocalityIdAndBlockedFalseAndRangingTrue(localityId)

    @Transactional(readOnly = true)
    override fun findByLocality(localityId: Long): List<Institution> =
        institutionRepository.findByLocalityIdAndBlockedFalse(localityId)

    @Transactional(readOnly = true)
    override fun findByLocalityForEvents(localityId: Long): List<Institution> =
        institutionRepository.findByLocalityId(localityId)

    @Transactional(readOnly = true)
    override fun findProcessingInstitutions(): List<Institution> =
        institutionRepository.findByProcessingTrue()

}