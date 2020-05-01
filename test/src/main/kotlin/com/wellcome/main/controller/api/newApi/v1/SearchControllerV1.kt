package com.wellcome.main.controller.api.newApi.v1

import com.wellcome.main.component.FirebaseAuthProvider
import com.wellcome.main.dto.api.newDto.response.v1.DynamicSearchResponseV1
import com.wellcome.main.dto.api.newDto.response.v1.DynamicSearchResultResponseV1
import com.wellcome.main.dto.api.newDto.response.v1.SearchResultResponseV1
import com.wellcome.main.dto.api.paths.PathsV1
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
@RequestMapping(value = [PathsV1.BASE_SEARCH])
open class SearchControllerV1 @Autowired constructor(
    private val firebaseAuthProvider: FirebaseAuthProvider,
    private val searchManagementService: SearchManagementService
) {

    @GetMapping(value = [PathsV1.Search.GET_SEARCH_ATTRIBUTES])
    open fun getDynamicSearchAttributes(): DynamicSearchResponseV1 {
        val token = getHeader(RequestKey.TOKEN)
        val googleUid = token?.let(firebaseAuthProvider::getUserCredentialsByFirebaseToken)?.googleUid
        val reviewing = getHeader(RequestKey.REVIEWING)?.toBoolean() ?: throw Exception("Require review flag")
        return searchManagementService.getDynamicSearchAttributesV1(reviewing, 5, googleUid)
    }

    @GetMapping(value = [PathsV1.Search.SEARCH])
    open fun search(@RequestParam(value = QueryString.CATEGORY_ID) categoryId: Long,
                    @RequestParam(value = QueryString.DAY) day: String): DynamicSearchResultResponseV1 {
        val token = getHeader(RequestKey.TOKEN)
        val googleUid = token?.let(firebaseAuthProvider::getUserCredentialsByFirebaseToken)?.googleUid
        val reviewing = getHeader(RequestKey.REVIEWING)?.toBoolean() ?: throw Exception("Require review flag")
        return searchManagementService.searchV1(reviewing, categoryId, day, 5, googleUid)
    }

    @GetMapping(value = [PathsV1.Search.SEARCH_BY_SIMILAR_NAME])
    open fun searchBySimilarName(@RequestParam(value = QueryString.INSTITUTION_SIMILAR_NAME) institutionSimilarName: String): SearchResultResponseV1 {
        val token = getHeader(RequestKey.TOKEN)
        val googleUid = token?.let(firebaseAuthProvider::getUserCredentialsByFirebaseToken)?.googleUid
        val reviewing = getHeader(RequestKey.REVIEWING)?.toBoolean() ?: throw Exception("Require review flag")
        return searchManagementService.searchBySimilarName(reviewing, institutionSimilarName, 5, googleUid)
    }

}