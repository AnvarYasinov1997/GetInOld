package com.wellcome.main.controller.admin

import com.wellcome.main.dto.admin.response.UserStoryResponse
import com.wellcome.main.service.management.admin.AdminUserStoryManagementService
import com.wellcome.main.util.enumerators.Permissions
import com.wellcome.main.util.variables.Paths
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = [Paths.BASE_USER_STORY])
open class AdminUserStoryController @Autowired constructor(
    private val adminUserStoryManagementService: AdminUserStoryManagementService
) {

    @Secured(value = [Permissions.PermissionValues.GET_ALL_USER_STORY])
    @GetMapping(value = [Paths.UserStory.GET_ALL])
    open fun getAll(): UserStoryResponse {
        return adminUserStoryManagementService.getAll()
    }

    @Secured(value = [Permissions.PermissionValues.APPROVE_USER_STORY])
    @GetMapping(value = [Paths.UserStory.APPROVE + "/{id}"])
    open fun approve(@PathVariable(value = "id") id: Long) {
        adminUserStoryManagementService.approve(id)
    }

    @Secured(value = [Permissions.PermissionValues.REMOVE_USER_STORY])
    @DeleteMapping(value = [Paths.UserStory.REMOVE + "/{id}"])
    open fun remove(@PathVariable(value = "id") id: Long) {
        adminUserStoryManagementService.remove(id)
    }

}