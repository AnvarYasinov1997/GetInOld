package com.wellcome.main.service.facade

import com.wellcome.main.entity.Locality
import com.wellcome.main.repository.local.postgre.LocalityRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface LocalityService : BaseService<Locality> {
    fun findByName(localityName: String): Locality?
}

@Service
open class DefaultLocalityService @Autowired constructor(
    private val localityRepository: LocalityRepository
) : LocalityService, DefaultBaseService<Locality>(Locality::class.java.simpleName, localityRepository) {

    @Transactional(readOnly = true)
    override fun findByName(localityName: String): Locality? =
        localityRepository.findByName(localityName).orElseGet { null }

}