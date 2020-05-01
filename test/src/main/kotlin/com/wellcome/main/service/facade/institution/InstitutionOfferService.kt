package com.wellcome.main.service.facade.institution

import com.wellcome.main.entity.institution.InstitutionOffer
import com.wellcome.main.repository.local.postgre.InstitutionOfferRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface InstitutionOfferService : BaseService<InstitutionOffer> {
    fun findNotCompleted(): List<InstitutionOffer>
    fun getActiveByInstitution(institutionId: Long): List<InstitutionOffer>
}

@Service
open class DefaultInstitutionOfferService @Autowired constructor(
    private val institutionOfferRepository: InstitutionOfferRepository
) : InstitutionOfferService,
    DefaultBaseService<InstitutionOffer>(InstitutionOffer::class.java.simpleName, institutionOfferRepository) {

    @Transactional(readOnly = true)
    override fun findNotCompleted(): List<InstitutionOffer> =
        institutionOfferRepository.findByCompletedFalse()

    @Transactional(readOnly = true)
    override fun getActiveByInstitution(institutionId: Long): List<InstitutionOffer> =
        institutionOfferRepository.findByInstitutionIdAndActiveTrue(institutionId)
            .filterNot(InstitutionOffer::inReview)

}