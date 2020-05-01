package com.wellcome.main.service.facade.institutionProfile

import com.wellcome.main.entity.institutionProfile.InstitutionEditRequestPicture
import com.wellcome.main.repository.local.postgre.InstitutionEditRequestPictureRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import com.wellcome.main.service.utils.LogMessage
import com.wellcome.main.service.utils.LoggerService
import com.wellcome.main.util.functions.ifNotEmpty
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface InstitutionEditRequestPictureService : BaseService<InstitutionEditRequestPicture> {
    fun findByPictureId(pictureId: Long): InstitutionEditRequestPicture?
}

@Service
open class DefaultInstitutionEditRequestPictureService constructor(
    private val loggerService: LoggerService,
    private val institutionEditRequestPictureRepository: InstitutionEditRequestPictureRepository
) : InstitutionEditRequestPictureService,
    DefaultBaseService<InstitutionEditRequestPicture>(InstitutionEditRequestPicture::class.java.simpleName, institutionEditRequestPictureRepository) {

    @Transactional(readOnly = true)
    override fun findByPictureId(pictureId: Long): InstitutionEditRequestPicture? =
        institutionEditRequestPictureRepository.findByPictureId(pictureId).ifNotEmpty()
            ?.let {
                if (it.size == 1) it.first()
                else it.last().also {
                    loggerService.sendLogDeveloper(LogMessage("InstitutionEditRequest is not in a single copy"))
                }
            }

}