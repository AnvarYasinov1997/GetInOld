package com.wellcome.main.controller.web

import com.wellcome.main.component.FirebaseAuthProvider
import com.wellcome.main.dto.api.paths.QueryString
import com.wellcome.main.dto.web.response.FeedResultStepOneResponse
import com.wellcome.main.dto.web.response.FeedResultStepThreeResponse
import com.wellcome.main.dto.web.response.FeedResultStepTwoResponse
import com.wellcome.main.service.management.web.WebFeedManagementService
import com.wellcome.main.util.functions.getHeader
import com.wellcome.main.util.variables.RequestKey
import com.wellcome.main.util.variables.WebPaths
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = [WebPaths.BASE_FEED])
open class WebFeedController @Autowired constructor(
    private val firebaseAuthProvider: FirebaseAuthProvider,
    private val feedManagementService: WebFeedManagementService
) {

    @GetMapping(value = [WebPaths.Feed.FEED_STEP_ONE])
    open fun feedStepOne(@RequestParam(value = QueryString.CATEGORY_ID) categoryId: Long,
                         @RequestParam(value = QueryString.DAY) day: String): FeedResultStepOneResponse {
        val token = getHeader(RequestKey.TOKEN)
        val googleUid = token?.let(firebaseAuthProvider::getUserCredentialsByFirebaseToken)?.googleUid
        return feedManagementService.feedStepOne(categoryId, day, 5, googleUid)
    }

    @GetMapping(value = [WebPaths.Feed.FEED_STEP_TWO])
    open fun feedStepTwo(@RequestParam(value = QueryString.CATEGORY_ID) categoryId: Long,
                         @RequestParam(value = QueryString.DAY) day: String): FeedResultStepTwoResponse {
        val token = getHeader(RequestKey.TOKEN)
        val googleUid = token?.let(firebaseAuthProvider::getUserCredentialsByFirebaseToken)?.googleUid
        return feedManagementService.feedStepTwo(categoryId, day, 5, googleUid)
    }

    @GetMapping(value = [WebPaths.Feed.FEED_STEP_THREE])
    open fun feedStepThree(@RequestParam(value = QueryString.CATEGORY_ID) categoryId: Long,
                           @RequestParam(value = QueryString.DAY) day: String): FeedResultStepThreeResponse {
        val token = getHeader(RequestKey.TOKEN)
        val googleUid = token?.let(firebaseAuthProvider::getUserCredentialsByFirebaseToken)?.googleUid
        return feedManagementService.feedStemThree(categoryId, day, 5, googleUid)
    }

}
