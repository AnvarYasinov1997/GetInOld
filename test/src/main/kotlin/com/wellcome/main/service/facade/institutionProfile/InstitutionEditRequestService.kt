package com.wellcome.main.service.facade.institutionProfile

import com.wellcome.main.entity.institutionProfile.InstitutionEditRequest
import com.wellcome.main.repository.local.postgre.InstitutionEditRequestRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import com.wellcome.main.service.utils.LogMessage
import com.wellcome.main.service.utils.LoggerService
import com.wellcome.main.util.functions.ifNotEmpty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface InstitutionEditRequestService : BaseService<InstitutionEditRequest> {
    fun findByInstitutionProfile(institutionProfileId: Long): InstitutionEditRequest?
}

@Service
open class DefaultInstitutionEditRequestService @Autowired constructor(
    private val loggerService: LoggerService,
    private val institutionEditRequestRepository: InstitutionEditRequestRepository
) : InstitutionEditRequestService,
    DefaultBaseService<InstitutionEditRequest>(InstitutionEditRequest::class.java.simpleName, institutionEditRequestRepository) {

    @Transactional(readOnly = true)
    override fun findByInstitutionProfile(institutionProfileId: Long): InstitutionEditRequest? =
        institutionEditRequestRepository.findByInstitutionProfileIdAndApprovedFalse(institutionProfileId).ifNotEmpty()
            ?.let {
                if (it.size == 1) it.first()
                else it.last().also {
                    loggerService.sendLogDeveloper(LogMessage("InstitutionEditRequest is not in a single copy"))
                }
            }

}