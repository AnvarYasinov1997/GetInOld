package com.wellcome.main.service.facade.institution

import com.wellcome.main.entity.institution.InstitutionWorkTime
import com.wellcome.main.repository.local.postgre.InstitutionWorkTimeRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

interface InstitutionWorkTimeService : BaseService<InstitutionWorkTime>

@Service
open class DefaultInstitutionWorkTimeService @Autowired constructor(
    private val institutionWorkTimeRepository: InstitutionWorkTimeRepository
) : InstitutionWorkTimeService,
    DefaultBaseService<InstitutionWorkTime>(InstitutionWorkTime::class.java.simpleName, institutionWorkTimeRepository)