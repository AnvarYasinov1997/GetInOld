package com.wellcome.main.service.facade.institution

import com.wellcome.main.entity.institution.InstitutionReview
import com.wellcome.main.repository.local.postgre.InstitutionReviewRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

interface InstitutionReviewService : BaseService<InstitutionReview>

@Service
open class DefaultInstitutionReviewService @Autowired constructor(
    private val institutionReviewRepository: InstitutionReviewRepository
) : InstitutionReviewService,
    DefaultBaseService<InstitutionReview>(InstitutionReview::class.java.simpleName, institutionReviewRepository)