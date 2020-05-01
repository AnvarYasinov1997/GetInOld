package com.wellcome.main.service.facade.venue.event

import com.wellcome.main.entity.venue.event.Event
import com.wellcome.main.repository.local.postgre.EventRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

interface EventService : BaseService<Event>

@Service
open class DefaultEventService @Autowired constructor(
    private val eventRepository: EventRepository
) : DefaultBaseService<Event>(Event::class.java.simpleName, eventRepository),
    EventService