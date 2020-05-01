package com.wellcome.main.controller.admin

import com.wellcome.main.dto.admin.response.LocalityResponse
import com.wellcome.main.service.management.admin.AdminLocalityManagementService
import com.wellcome.main.util.enumerators.Permissions
import com.wellcome.main.util.variables.Paths
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = [Paths.BASE_LOCALITY])
open class AdminLocalityController @Autowired constructor(
    private val adminLocalityManagementService: AdminLocalityManagementService
) {

    @Secured(value = [Permissions.PermissionValues.GET_LOCALITY])
    @GetMapping(value = [Paths.Locality.GET_ALL])
    open fun getAllLocalities(): LocalityResponse {
        return adminLocalityManagementService.getAllLocalities()
    }

}