package com.wellcome.main.service.scheduler

import com.wellcome.main.component.TimestampProvider
import com.wellcome.main.service.facade.institution.InstitutionEventService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
open class InstitutionEventSchedulerService @Autowired constructor(
    private val timestampProvider: TimestampProvider,
    private val institutionEventService: InstitutionEventService
) {

    @Scheduled(fixedDelay = 1000 * 60 * 20)
    fun checkStartOrCompleted() {
        val currentDate = timestampProvider.getUserZonedDateTime().toLocalDate()
        institutionEventService.findNotCompleted().forEach {
            if (LocalDate.parse(it.date) < currentDate)
                it.apply {
                    this.completed = true
                }.let(institutionEventService::saveOrUpdate)
        }
    }

}