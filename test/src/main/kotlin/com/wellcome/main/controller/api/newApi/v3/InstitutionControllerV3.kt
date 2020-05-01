package com.wellcome.main.controller.api.newApi.v3

import com.wellcome.main.component.FirebaseAuthProvider
import com.wellcome.main.dto.api.newDto.response.v3.ProfileResponseV3
import com.wellcome.main.dto.api.paths.PathsV3
import com.wellcome.main.dto.api.paths.QueryString
import com.wellcome.main.service.management.api.InstitutionManagementService
import com.wellcome.main.util.functions.getHeader
import com.wellcome.main.util.variables.RequestKey
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = [PathsV3.BASE_INSTITUTION])
open class InstitutionControllerV3 @Autowired constructor(
    private val firebaseAuthProvider: FirebaseAuthProvider,
    private val institutionManagementService: InstitutionManagementService
) {

    @GetMapping(value = [PathsV3.Institution.GET_PROFILE])
    open fun getProfile(@RequestParam(value = QueryString.INSTITUTION_ID) institutionId: Long): ProfileResponseV3 {
        val token = getHeader(RequestKey.TOKEN)
        val googleUid = token?.let(firebaseAuthProvider::getUserCredentialsByFirebaseToken)?.googleUid
        return institutionManagementService.getProfileV3(institutionId, googleUid)
    }

}