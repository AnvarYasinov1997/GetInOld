package com.wellcome.main.service.management.web

import com.wellcome.main.dto.web.response.AllEventResponse
import com.wellcome.main.dto.web.response.EventResponse
import com.wellcome.main.service.extentions.generators.web.common.generateEventDto
import com.wellcome.main.service.extentions.generators.web.common.generateEventDtoList
import com.wellcome.main.service.facade.institution.InstitutionEventService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate


interface WebEventManagementService {
    fun getById(id: Long): EventResponse
    fun getAll(): AllEventResponse
}

@Service
open class DefaultWebEventManagementService @Autowired constructor(
    private val institutionEventService: InstitutionEventService
): WebEventManagementService {

    @Transactional(readOnly = true)
    override fun getById(id: Long): EventResponse {
        val event = institutionEventService.findById(id)

        return EventResponse(event.generateEventDto())
    }

    @Transactional(readOnly = true)
    override fun getAll(): AllEventResponse {
        val events = institutionEventService.findNotCompleted()
            .sortedBy { LocalDate.parse(it.date) }

        return AllEventResponse(events.generateEventDtoList())
    }

}