package com.wellcome.main.service.facade.institutionProfile

import com.wellcome.main.entity.institutionProfile.InstitutionEditRequestStatus
import com.wellcome.main.repository.local.postgre.InstitutionEditRequestStatusRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.stereotype.Service

interface InstitutionEditRequestStatusService : BaseService<InstitutionEditRequestStatus>

@Service
open class DefaultInstitutionEditRequestStatusService constructor(
    private val institutionEditRequestStatusRepository: InstitutionEditRequestStatusRepository
) : InstitutionEditRequestStatusService,
    DefaultBaseService<InstitutionEditRequestStatus>(InstitutionEditRequestStatus::class.java.simpleName, institutionEditRequestStatusRepository)