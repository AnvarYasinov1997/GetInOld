package com.wellcome.main.controller.admin

import com.wellcome.main.dto.admin.request.ChangePasswordRequest
import com.wellcome.main.service.management.admin.AdminUserManagementService
import com.wellcome.main.util.enumerators.Permissions
import com.wellcome.main.util.variables.Paths
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = [Paths.BASE_USER])
open class AdminUserController @Autowired constructor(
    private val adminUserManagementService: AdminUserManagementService
) {

    @Secured(value = [Permissions.PermissionValues.CHANGE_PASSWORD])
    @PostMapping(value = [Paths.User.CHANGE_PASSWORD])
    open fun changePassword(@RequestBody changePasswordRequest: ChangePasswordRequest) {
        adminUserManagementService.changePassword(changePasswordRequest.email, changePasswordRequest.newPassword)
    }

}