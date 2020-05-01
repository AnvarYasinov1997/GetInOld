package com.wellcome.main.controller.admin

import com.wellcome.main.dto.admin.response.AllSelectionResponse
import com.wellcome.main.dto.api.paths.QueryString
import com.wellcome.main.service.management.admin.AdminSelectionManagementService
import com.wellcome.main.util.enumerators.Permissions
import com.wellcome.main.util.variables.Paths
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = [Paths.BASE_SELECTION])
open class AdminSelectionController @Autowired constructor(
    private val adminSelectionManagementService: AdminSelectionManagementService
) {

    @Secured(value = [Permissions.PermissionValues.ADD_SELECTION])
    @GetMapping(value = [Paths.Selection.ADD])
    open fun add(@RequestParam(value = QueryString.SELECTION_NAME) selectionName: String) {
        adminSelectionManagementService.add(selectionName)
    }

    @Secured(value = [Permissions.PermissionValues.GET_ALL_SELECTIONS])
    @GetMapping(value = [Paths.Selection.GET_ALL])
    open fun getAll(): AllSelectionResponse {
        return adminSelectionManagementService.getAll()
    }

    @Secured(value = [Permissions.PermissionValues.REMOVE_SELECTION])
    @DeleteMapping(value = [Paths.Selection.REMOVE])
    open fun remove(@RequestParam(value = QueryString.SELECTION_ID) selectionId: Long) {
        adminSelectionManagementService.remove(selectionId)
    }

}