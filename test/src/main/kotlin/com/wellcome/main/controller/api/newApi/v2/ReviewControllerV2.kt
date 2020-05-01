package com.wellcome.main.controller.api.newApi.v2

import com.wellcome.main.component.FirebaseAuthProvider
import com.wellcome.main.dto.api.newDto.request.v1.ReviewRequestV1
import com.wellcome.main.dto.api.newDto.response.v2.ReviewActionResponseV2
import com.wellcome.main.dto.api.paths.PathsV2
import com.wellcome.main.exception.UnauthorizedException
import com.wellcome.main.service.management.api.ReviewManagementService
import com.wellcome.main.util.functions.getHeader
import com.wellcome.main.util.variables.RequestKey
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = [PathsV2.BASE_REVIEW])
open class ReviewControllerV2 @Autowired constructor(
    private val firebaseAuthProvider: FirebaseAuthProvider,
    private val reviewManagementService: ReviewManagementService
) {

    @PostMapping(value = [PathsV2.Review.SAVE])
    open fun saveReview(@RequestBody request: ReviewRequestV1): ReviewActionResponseV2 {
        val token = getHeader(RequestKey.TOKEN) ?: throw UnauthorizedException("Token is null")
        val initResult = firebaseAuthProvider.getUserCredentialsByFirebaseToken(token)
        return reviewManagementService.saveReviewV2(request.institutionId, request.starCount, request.feedback, initResult.googleUid)
    }

}