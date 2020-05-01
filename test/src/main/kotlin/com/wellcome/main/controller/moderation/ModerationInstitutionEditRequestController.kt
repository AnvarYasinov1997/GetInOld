package com.wellcome.main.controller.moderation

import com.wellcome.main.annotations.PreProcessor
import com.wellcome.main.dto.moderation.request.v1.InstitutionEditRequestRequestV1
import com.wellcome.main.service.management.moderation.ModerationInstitutionEditRequestManagementService
import com.wellcome.main.util.functions.getProfileContext
import com.wellcome.main.util.variables.ModerationPathsV1
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = [ModerationPathsV1.BASE_INSTITUTION_EDIT_REQUEST])
open class ModerationInstitutionEditRequestController @Autowired constructor(
    private val moderationInstitutionEditRequestManagementService: ModerationInstitutionEditRequestManagementService
) {

    @PreProcessor
    @PostMapping(value = [ModerationPathsV1.InstitutionEditRequest.EDIT])
    open fun edit(@RequestBody request: InstitutionEditRequestRequestV1) {
        getProfileContext().let(::requireNotNull).profileModels
            .firstOrNull { it.institutionProfileId == request.institutionId }
            ?: throw AccessDeniedException("User has not access for institution with id ${request.institutionId}")
        return moderationInstitutionEditRequestManagementService.edit(request)
    }

}