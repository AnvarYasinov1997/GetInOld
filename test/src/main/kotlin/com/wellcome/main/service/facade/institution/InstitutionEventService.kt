package com.wellcome.main.service.facade.institution

import com.wellcome.main.entity.institution.InstitutionEvent
import com.wellcome.main.repository.local.postgre.InstitutionEventRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface InstitutionEventService : BaseService<InstitutionEvent> {
    fun findNotCompleted(): List<InstitutionEvent>
}

@Service
open class DefaultInstitutionEventService @Autowired constructor(
    private val institutionEventRepository: InstitutionEventRepository
) : InstitutionEventService,
    DefaultBaseService<InstitutionEvent>(InstitutionEvent::class.java.simpleName, institutionEventRepository) {

    @Transactional(readOnly = true)
    override fun findNotCompleted(): List<InstitutionEvent> =
        institutionEventRepository.findByCompletedFalse()
            .filterNot(InstitutionEvent::inReview)

}