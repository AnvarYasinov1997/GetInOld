package com.wellcome.main.controller.admin

import com.wellcome.main.dto.admin.response.CategoryNameResponse
import com.wellcome.main.service.management.admin.AdminCategoryManagementService
import com.wellcome.main.util.enumerators.Permissions
import com.wellcome.main.util.variables.Paths
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = [Paths.BASE_CATEGORY])
open class AdminCategoryController @Autowired constructor(
    private val adminCategoryManagementService: AdminCategoryManagementService
) {

    @Secured(value = [Permissions.PermissionValues.GET_CATEGORY_NAMES])
    @GetMapping(value = [Paths.Category.GET_CATEGORY_NAMES])
    open fun getCategoryNames(): CategoryNameResponse {
        return adminCategoryManagementService.getCategories()
    }

}