package com.wellcome.main.controller.admin

import com.wellcome.main.dto.admin.request.AddInstitutionRequest
import com.wellcome.main.dto.admin.request.EditInstitutionRequest
import com.wellcome.main.dto.admin.response.InstitutionModerationResponse
import com.wellcome.main.dto.admin.response.InstitutionNameResponse
import com.wellcome.main.service.management.admin.AdminInstitutionManagementService
import com.wellcome.main.util.enumerators.Permissions
import com.wellcome.main.util.functions.getUserId
import com.wellcome.main.util.variables.Paths
import com.wellcome.main.util.variables.Query
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping(value = [Paths.BASE_INSTITUTION])
open class AdminInstitutionController @Autowired constructor(
    private val adminInstitutionManagementService: AdminInstitutionManagementService
) {

    @Secured(value = [Permissions.PermissionValues.GET_INSTITUTION_BY_ID])
    @GetMapping(value = [Paths.Institution.GET_BY_ID + "/{id}"])
    open fun getById(@PathVariable(value = "id") id: Long): InstitutionModerationResponse {
        return adminInstitutionManagementService.getById(id)
    }

    @Secured(value = [Permissions.PermissionValues.GET_INSTITUTION_NAMES])
    @GetMapping(value = [Paths.Institution.GET_NAMES])
    open fun getNames(@RequestParam(value = Query.LOCALITY_ID) localityId: Long,
                      @RequestParam(value = Query.CATEGORY_ID) categoryId: Long): InstitutionNameResponse {
        return adminInstitutionManagementService.getNames(categoryId, localityId)
    }

    @Secured(value = [Permissions.PermissionValues.GET_INSTITUTION_NAMES])
    @GetMapping(value = [Paths.Institution.GET_ALL_NAMES])
    open fun getAllNames(@RequestParam(value = Query.LOCALITY_ID) localityId: Long): InstitutionNameResponse {
        return adminInstitutionManagementService.getAllNames(localityId)
    }

    @Secured(value = [Permissions.PermissionValues.GET_ONE_INSTITUTION])
    @GetMapping(value = [Paths.Institution.GET_ONE_BY_LOCALITY])
    open fun getOneByLocality(@RequestParam(value = Query.LOCALITY_ID) localityId: Long): InstitutionModerationResponse {
        return adminInstitutionManagementService.getOneByLocality(localityId)
    }

    @Secured(value = [Permissions.PermissionValues.CREATE_INSTITUTION])
    @PostMapping(value = [Paths.Institution.CREATE_FROM_FILE])
    open fun createFromFile(@RequestParam(value = Query.LOCALITY_ID) localityId: Long,
                            @RequestParam(value = Query.DATA_FILE) dataFile: MultipartFile) {
        adminInstitutionManagementService.createFromFile(dataFile, localityId)
    }

    @Secured(value = [Permissions.PermissionValues.SAVE_INSTITUTION])
    @PostMapping(value = [Paths.Institution.ADD])
    open fun add(@RequestParam(value = Query.LOCALITY_ID) localityId: Long,
                 @RequestBody request: AddInstitutionRequest) {
        adminInstitutionManagementService.add(request, localityId, requireNotNull(getUserId()))
    }

    @Secured(value = [Permissions.PermissionValues.UPDATE_INSTITUTION])
    @PostMapping(value = [Paths.Institution.EDIT])
    open fun edit(@RequestBody request: EditInstitutionRequest) {
        adminInstitutionManagementService.edit(request, requireNotNull(getUserId()))
    }

    @Secured(value = [Permissions.PermissionValues.CREATE_INSTITUTION])
    @GetMapping(value = [Paths.Institution.FILL])
    open fun fill() {
        adminInstitutionManagementService.fill(requireNotNull(getUserId()))
    }

    @Secured(value = [Permissions.PermissionValues.BLOCK_INSTITUTION])
    @GetMapping(value = [Paths.Institution.BLOCK])
    open fun block(@RequestParam(value = Query.INSTITUTION_ID) institutionId: Long) {
        adminInstitutionManagementService.block(institutionId, requireNotNull(getUserId()))
    }

}