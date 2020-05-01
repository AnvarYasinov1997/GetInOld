package com.wellcome.main.controller.api.newApi.v1

import com.wellcome.main.component.FirebaseAuthProvider
import com.wellcome.main.dto.api.newDto.response.v1.FeedResultStepOneResponseV1
import com.wellcome.main.dto.api.newDto.response.v1.FeedResultStepThreeResponseV1
import com.wellcome.main.dto.api.newDto.response.v1.FeedResultStepTwoResponseV1
import com.wellcome.main.dto.api.paths.PathsV1
import com.wellcome.main.dto.api.paths.QueryString
import com.wellcome.main.service.management.api.FeedManagementService
import com.wellcome.main.util.functions.getHeader
import com.wellcome.main.util.variables.RequestKey
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping(value = [PathsV1.BASE_FEED])
open class FeedControllerV1 @Autowired constructor(
    private val firebaseAuthProvider: FirebaseAuthProvider,
    private val feedManagementService: FeedManagementService
) {

    @GetMapping(value = [PathsV1.Search.FEED_STEP_ONE])
    open fun feedStepOne(@RequestParam(value = QueryString.CATEGORY_ID) categoryId: Long,
                         @RequestParam(value = QueryString.DAY) day: String): FeedResultStepOneResponseV1 {
        val token = getHeader(RequestKey.TOKEN)
        val googleUid = token?.let(firebaseAuthProvider::getUserCredentialsByFirebaseToken)?.googleUid
        val reviewing = getHeader(RequestKey.REVIEWING)?.toBoolean() ?: throw Exception("Require review flag")
        return feedManagementService.feedStepOne(reviewing, categoryId, day, 5, googleUid)
    }

    @GetMapping(value = [PathsV1.Search.FEED_STEP_TWO])
    open fun feedStepTwo(@RequestParam(value = QueryString.CATEGORY_ID) categoryId: Long,
                         @RequestParam(value = QueryString.DAY) day: String): FeedResultStepTwoResponseV1 {
        val token = getHeader(RequestKey.TOKEN)
        val googleUid = token?.let(firebaseAuthProvider::getUserCredentialsByFirebaseToken)?.googleUid
        val reviewing = getHeader(RequestKey.REVIEWING)?.toBoolean() ?: throw Exception("Require review flag")

        return feedManagementService.feedStepTwo(reviewing, categoryId, day, 5, googleUid)
    }

    @GetMapping(value = [PathsV1.Search.FEED_STEP_THREE])
    open fun feedStepThree(@RequestParam(value = QueryString.CATEGORY_ID) categoryId: Long,
                           @RequestParam(value = QueryString.DAY) day: String): FeedResultStepThreeResponseV1 {
        val token = getHeader(RequestKey.TOKEN)
        val googleUid = token?.let(firebaseAuthProvider::getUserCredentialsByFirebaseToken)?.googleUid
        val reviewing = getHeader(RequestKey.REVIEWING)?.toBoolean() ?: throw Exception("Require review flag")

        return feedManagementService.feedStemThree(reviewing, categoryId, day, 5, googleUid)
    }

}