package com.wellcome.main.service.facade.institution

import com.wellcome.main.entity.institution.InstitutionTag
import com.wellcome.main.repository.local.postgre.InstitutionTagRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

interface InstitutionTagService : BaseService<InstitutionTag>

@Service
open class DefaultInstitutionTagService @Autowired constructor(
    private val institutionTagRepository: InstitutionTagRepository
) : InstitutionTagService,
    DefaultBaseService<InstitutionTag>(InstitutionTag::class.java.simpleName, institutionTagRepository)