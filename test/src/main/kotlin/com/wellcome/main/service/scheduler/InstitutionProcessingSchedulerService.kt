package com.wellcome.main.service.scheduler

import com.wellcome.main.service.facade.institution.InstitutionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
open class InstitutionProcessingSchedulerService @Autowired constructor(
    private val institutionService: InstitutionService
) {

    @Scheduled(cron = "0 1 0 * * *")
    open fun checkProcessingInstitutions() {
        val processingInstitution = institutionService.findProcessingInstitutions()
        processingInstitution.forEach {
            if (!it.blocked) {
                it.apply {
                    this.processing = false
                }.let(institutionService::saveOrUpdate)
            }
        }
    }


}