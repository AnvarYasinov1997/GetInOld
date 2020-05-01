package com.wellcome.main.service.facade.institution

import com.wellcome.main.entity.institution.InstitutionOfferWorkTime
import com.wellcome.main.repository.local.postgre.InstitutionOfferWorkTimeRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

interface InstitutionOfferWorkTimeService : BaseService<InstitutionOfferWorkTime>

@Service
open class DefaultInstitutionOfferWorkTimeService @Autowired constructor(
    private val institutionOfferWorkTimeRepository: InstitutionOfferWorkTimeRepository
) : InstitutionOfferWorkTimeService,
    DefaultBaseService<InstitutionOfferWorkTime>(InstitutionOfferWorkTime::class.java.simpleName, institutionOfferWorkTimeRepository)