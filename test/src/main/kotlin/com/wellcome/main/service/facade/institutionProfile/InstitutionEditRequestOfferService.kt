package com.wellcome.main.service.facade.institutionProfile

import com.wellcome.main.entity.institutionProfile.InstitutionEditRequestOffer
import com.wellcome.main.repository.local.postgre.InstitutionEditRequestOfferRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import com.wellcome.main.service.utils.LogMessage
import com.wellcome.main.service.utils.LoggerService
import com.wellcome.main.util.functions.ifNotEmpty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface InstitutionEditRequestOfferService : BaseService<InstitutionEditRequestOffer> {
    fun findByEvent(offerId: Long): InstitutionEditRequestOffer?
}

@Service
open class DefaultInstitutionEditRequestOfferService @Autowired constructor(
    private val loggerService: LoggerService,
    private val institutionEditRequestOfferRepository: InstitutionEditRequestOfferRepository
) : InstitutionEditRequestOfferService,
    DefaultBaseService<InstitutionEditRequestOffer>(InstitutionEditRequestOffer::class.java.simpleName, institutionEditRequestOfferRepository) {

    @Transactional(readOnly = true)
    override fun findByEvent(offerId: Long): InstitutionEditRequestOffer? =
        institutionEditRequestOfferRepository.findByOfferId(offerId).ifNotEmpty()
            ?.let {
                if (it.size == 1) it.first()
                else it.last().also {
                    loggerService.sendLogDeveloper(LogMessage("InstitutionEditRequest is not in a single copy"))
                }
            }

}