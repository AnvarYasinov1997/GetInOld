package com.wellcome.main.controller.api.newApi.v1

import com.wellcome.main.component.FirebaseAuthProvider
import com.wellcome.main.dto.api.newDto.response.v1.ClosestInstitutionResponseV1
import com.wellcome.main.dto.api.newDto.response.v1.InstitutionMapResponseV1
import com.wellcome.main.dto.api.newDto.response.v1.ProfileResponseV1
import com.wellcome.main.dto.api.paths.PathsV1
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
@RequestMapping(value = [PathsV1.BASE_INSTITUTION])
open class InstitutionControllerV1 @Autowired constructor(
    private val firebaseAuthProvider: FirebaseAuthProvider,
    private val institutionManagementService: InstitutionManagementService
) {

    @GetMapping(value = [PathsV1.Institution.GET_PROFILE])
    open fun getInstitutionProfile(@RequestParam(value = QueryString.INSTITUTION_ID) institutionId: Long): ProfileResponseV1 {
        val token = getHeader(RequestKey.TOKEN)
        val googleUid = token?.let(firebaseAuthProvider::getUserCredentialsByFirebaseToken)?.googleUid
        return institutionManagementService.getProfileV1(institutionId, googleUid)
    }

    @GetMapping(value = [PathsV1.Institution.GET_CLOSEST])
    open fun getClosestInstitutions(@RequestParam(value = QueryString.LAT) lat: Double,
                                    @RequestParam(value = QueryString.LON) lon: Double,
                                    @RequestParam(value = QueryString.CATEGORY_ID) categoryId: Long,
                                    @RequestParam(value = QueryString.DAY) day: String): ClosestInstitutionResponseV1 {
        val token = getHeader(RequestKey.TOKEN)
        val googleUid = token?.let(firebaseAuthProvider::getUserCredentialsByFirebaseToken)?.googleUid
        val reviewing = getHeader(RequestKey.REVIEWING)?.toBoolean() ?: throw Exception("Require review flag")
        return institutionManagementService.getClosestV1(5, lat, lon, categoryId, day, googleUid, reviewing)
    }

    @GetMapping(value = [PathsV1.Institution.GET_FOR_MAP])
    open fun getInstitutionsForMap(): InstitutionMapResponseV1 {
        val reviewing = getHeader(RequestKey.REVIEWING)?.toBoolean()
            ?: throw Exception("Require review flag")
        return institutionManagementService.getForMap(5, reviewing)
    }

}