package com.wellcome.main.service.facade.institution

import com.wellcome.main.entity.institution.MapsInstitution
import com.wellcome.main.repository.local.postgre.MapsInstitutionRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface MapsInstitutionService : BaseService<MapsInstitution> {
    fun findAllNonCreated(): List<MapsInstitution>
}

@Service
open class DefaultMapsInstitutionService @Autowired constructor(
    private val mapsInstitutionRepository: MapsInstitutionRepository
) : MapsInstitutionService,
    DefaultBaseService<MapsInstitution>(MapsInstitution::class.java.simpleName, mapsInstitutionRepository) {

    @Transactional(readOnly = true)
    override fun findAllNonCreated(): List<MapsInstitution> =
        mapsInstitutionRepository.findByCreatedFalse()
}