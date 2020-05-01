package com.wellcome.main.service.management.admin

import com.wellcome.main.dto.admin.response.LocalityDto
import com.wellcome.main.dto.admin.response.LocalityResponse
import com.wellcome.main.service.facade.LocalityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface AdminLocalityManagementService {
    fun getAllLocalities(): LocalityResponse
}

@Service
open class DefaultAdminLocalityManagementService @Autowired constructor(
    private val localityService: LocalityService
) : AdminLocalityManagementService {

    @Transactional(readOnly = true)
    override fun getAllLocalities(): LocalityResponse =
        LocalityResponse(localityService.findAll().map {
            LocalityDto(it.id!!, it.name)
        })

}