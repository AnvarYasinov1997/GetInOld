package com.wellcome.main.controller.api.newApi.v3

import com.wellcome.main.component.FirebaseAuthProvider
import com.wellcome.main.dto.api.newDto.response.v3.DynamicSearchResponseV3
import com.wellcome.main.dto.api.newDto.response.v3.DynamicSearchResultResponseV3
import com.wellcome.main.dto.api.paths.PathsV3
import com.wellcome.main.dto.api.paths.QueryString
import com.wellcome.main.service.management.api.SearchManagementService
import com.wellcome.main.util.functions.getHeader
import com.wellcome.main.util.variables.RequestKey
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = [PathsV3.BASE_SEARCH])
open class SearchControllerV3 @Autowired constructor(
    private val firebaseAuthProvider: FirebaseAuthProvider,
    private val searchManagementService: SearchManagementService
) {

    @GetMapping(value = [PathsV3.Search.GET_SEARCH_ATTRIBUTES])
    open fun getDynamicSearchAttributes(): DynamicSearchResponseV3 {
        val token = getHeader(RequestKey.TOKEN)
        val googleUid = token?.let(firebaseAuthProvider::getUserCredentialsByFirebaseToken)?.googleUid
        val reviewing = getHeader(RequestKey.REVIEWING)?.toBoolean() ?: throw Exception("Require review flag")
        return searchManagementService.getDynamicSearchAttributesV3(reviewing, 5, googleUid)
    }

    @GetMapping(value = [PathsV3.Search.SEARCH])
    open fun search(@RequestParam(value = QueryString.CATEGORY_ID) categoryId: Long,
                    @RequestParam(value = QueryString.DAY) day: String): DynamicSearchResultResponseV3 {
        val token = getHeader(RequestKey.TOKEN)
        val googleUid = token?.let(firebaseAuthProvider::getUserCredentialsByFirebaseToken)?.googleUid
        val reviewing = getHeader(RequestKey.REVIEWING)?.toBoolean() ?: throw Exception("Require review flag")
        return searchManagementService.searchV3(reviewing, categoryId, day, 5, googleUid)
    }

}