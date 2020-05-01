package com.wellcome.main.controller.admin

import com.wellcome.main.dto.admin.response.InstitutionAttributesResponse
import com.wellcome.main.service.management.admin.AdminInstitutionAttributesManagementService
import com.wellcome.main.util.enumerators.Permissions
import com.wellcome.main.util.variables.Paths
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = [Paths.BASE_INSTITUTION_ATTRIBUTES])
open class
AdminInstitutionAttributesController @Autowired constructor(
    private val adminInstitutionAttributesManagementService: AdminInstitutionAttributesManagementService
) {

    @Secured(value = [Permissions.PermissionValues.GET_INSTITUTION_ATTRIBUTES])
    @GetMapping(value = [Paths.InstitutionAttributes.GET])
    open fun get(): InstitutionAttributesResponse {
        return adminInstitutionAttributesManagementService.getAttributes()
    }

}