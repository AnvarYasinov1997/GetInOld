package com.wellcome.main.service.facade.institution

import com.wellcome.main.entity.institution.InstitutionContactPhone
import com.wellcome.main.repository.local.postgre.InstitutionContactPhoneRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

interface InstitutionContactPhoneService : BaseService<InstitutionContactPhone>

@Service
open class DefaultInstitutionContactPhoneService @Autowired constructor(
    private val institutionContactPhoneRepository: InstitutionContactPhoneRepository
) : InstitutionContactPhoneService,
    DefaultBaseService<InstitutionContactPhone>(InstitutionContactPhone::class.java.simpleName, institutionContactPhoneRepository)