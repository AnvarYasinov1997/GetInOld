package com.wellcome.main.service.facade.institution

import com.wellcome.main.entity.institution.InstitutionPicture
import com.wellcome.main.repository.local.postgre.InstitutionPictureRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface InstitutionPictureService : BaseService<InstitutionPicture> {
    fun findByInstitutionId(institutionId: Long): List<InstitutionPicture>
}

@Service
open class DefaultInstitutionPictureService @Autowired constructor(
    private val institutionPictureRepository: InstitutionPictureRepository
) : InstitutionPictureService,
    DefaultBaseService<InstitutionPicture>(InstitutionPicture::class.java.simpleName, institutionPictureRepository) {

    @Transactional(readOnly = true)
    override fun findByInstitutionId(institutionId: Long): List<InstitutionPicture> =
        institutionPictureRepository.findByInstitutionId(institutionId)
            .filterNot(InstitutionPicture::inReview)

}