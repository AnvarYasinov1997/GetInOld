package com.wellcome.main.service.facade.institutionProfile

import com.wellcome.main.entity.institutionProfile.InstitutionEditRequestEvent
import com.wellcome.main.repository.local.postgre.InstitutionEditRequestEventRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import com.wellcome.main.service.utils.LogMessage
import com.wellcome.main.service.utils.LoggerService
import com.wellcome.main.util.functions.ifNotEmpty
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface InstitutionEditRequestEventService : BaseService<InstitutionEditRequestEvent> {
    fun findByEvent(eventId: Long): InstitutionEditRequestEvent?
}

@Service
open class DefaultInstitutionEditRequestDeleteEventService constructor(
    private val loggerService: LoggerService,
    private val institutionEditRequestEventRepository: InstitutionEditRequestEventRepository
) : InstitutionEditRequestEventService,
    DefaultBaseService<InstitutionEditRequestEvent>(InstitutionEditRequestEvent::class.java.simpleName, institutionEditRequestEventRepository) {

    @Transactional(readOnly = true)
    override fun findByEvent(eventId: Long): InstitutionEditRequestEvent? =
        institutionEditRequestEventRepository.findByEventId(eventId).ifNotEmpty()
            ?.let {
                if (it.size == 1) it.first()
                else it.last().also {
                    loggerService.sendLogDeveloper(LogMessage("InstitutionEditRequest is not in a single copy"))
                }
            }


}