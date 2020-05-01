package com.wellcome.main.service.scheduler

import com.wellcome.main.component.TimestampProvider
import com.wellcome.main.service.facade.institution.InstitutionOfferService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
open class InstitutionOfferSchedulerService @Autowired constructor(
    private val timestampProvider: TimestampProvider,
    private val institutionOfferService: InstitutionOfferService
) {

//    @Scheduled(fixedDelay = 1000 * 60 * 60)
    open fun checkStartOrCompleted() {
        val currentDate = timestampProvider.getUserZonedDateTime().toLocalDate()
        institutionOfferService.findNotCompleted().forEach {
            if (LocalDate.parse(it.startDate) >= currentDate)
                it.apply { this.active = true }.let(institutionOfferService::saveOrUpdate)
            if (LocalDate.parse(it.endDate) < currentDate)
                it.apply {
                    this.completed = true
                    this.active = false
                }.let(institutionOfferService::saveOrUpdate)
        }
    }

}