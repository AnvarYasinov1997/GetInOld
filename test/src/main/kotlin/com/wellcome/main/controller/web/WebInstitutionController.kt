package com.wellcome.main.controller.web

import com.wellcome.main.component.FirebaseAuthProvider
import com.wellcome.main.dto.api.paths.QueryString
import com.wellcome.main.dto.web.response.InstitutionProfileResponse
import com.wellcome.main.service.management.web.WebInstitutionManagementService
import com.wellcome.main.util.functions.getHeader
import com.wellcome.main.util.variables.RequestKey
import com.wellcome.main.util.variables.WebPaths
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = [WebPaths.BASE_INSTITUTION])
open class WebInstitutionController @Autowired constructor(
    private val firebaseAuthProvider: FirebaseAuthProvider,
    private val webInstitutionManagementService: WebInstitutionManagementService
) {

    @GetMapping(value = [WebPaths.Institution.GET_PROFILE])
    open fun getProfile(@RequestParam(value = QueryString.INSTITUTION_ID) institutionId: Long): InstitutionProfileResponse {
        val token = getHeader(RequestKey.TOKEN)
        val googleUid = token?.let(firebaseAuthProvider::getUserCredentialsByFirebaseToken)?.googleUid
        return webInstitutionManagementService.getProfile(institutionId, googleUid)
    }

}