package com.wellcome.main.service.facade.venue.event

import com.wellcome.main.entity.venue.event.EventContent
import com.wellcome.main.repository.local.postgre.EventContentRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

interface EventContentService : BaseService<EventContent>

@Service
open class DefaultEventContentService @Autowired constructor(
    private val eventContentRepository: EventContentRepository
) : DefaultBaseService<EventContent>(EventContent::class.java.simpleName, eventContentRepository),
    EventContentService