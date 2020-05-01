package com.wellcome.main.controller.admin

import com.wellcome.main.dto.admin.request.AddStoryRequest
import com.wellcome.main.dto.admin.response.AllStoryResponse
import com.wellcome.main.dto.admin.response.StoryTypeResponse
import com.wellcome.main.dto.api.paths.QueryString
import com.wellcome.main.service.management.admin.AdminStoryManagementService
import com.wellcome.main.util.enumerators.Permissions
import com.wellcome.main.util.variables.Paths
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = [Paths.BASE_STORY])
open class AdminStoryController @Autowired constructor(
    private val adminStoryManagementService: AdminStoryManagementService
) {

    @Secured(value = [Permissions.PermissionValues.GET_ALL_STORY])
    @GetMapping(value = [Paths.Story.GET_ALL])
    open fun getAll(@RequestParam(value = QueryString.STORY_TYPE) storyType: String): AllStoryResponse {
        return adminStoryManagementService.getAll(storyType)
    }

    @Secured(value = [Permissions.PermissionValues.SELECT_OFFER])
    @PostMapping(value = [Paths.Story.ADD])
    open fun add(@RequestBody request: AddStoryRequest) {
        adminStoryManagementService.add(request)
    }

    @GetMapping(value = [Paths.Story.GET_TYPES])
    open fun getTypes(): StoryTypeResponse {
        return adminStoryManagementService.getTypes()
    }

}