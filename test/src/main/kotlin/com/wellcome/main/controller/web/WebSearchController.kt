package com.wellcome.main.controller.web

import com.wellcome.main.component.FirebaseAuthProvider
import com.wellcome.main.dto.api.paths.QueryString
import com.wellcome.main.dto.web.response.DynamicSearchResponse
import com.wellcome.main.service.management.web.WebSearchManagementService
import com.wellcome.main.util.functions.getHeader
import com.wellcome.main.util.variables.RequestKey
import com.wellcome.main.util.variables.WebPaths
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = [WebPaths.BASE_SEARCH])
open class WebSearchController @Autowired constructor(
    private val firebaseAuthProvider: FirebaseAuthProvider,
    private val webSearchManagementService: WebSearchManagementService
){

    @GetMapping(value = [WebPaths.Search.GET_DYNAMIC_SEARCH])
    open fun getDynamicSearch(@RequestParam(value = QueryString.LOCALITY_ID) localityId: Long): DynamicSearchResponse {
        val token = getHeader(RequestKey.TOKEN)
        val googleUid =
            token?.let { firebaseAuthProvider.getUserCredentialsByFirebaseToken(it) }?.googleUid
        return webSearchManagementService.getDynamicSearch(googleUid, localityId)
    }

}