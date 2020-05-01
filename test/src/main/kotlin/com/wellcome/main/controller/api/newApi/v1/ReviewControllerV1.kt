package com.wellcome.main.controller.api.newApi.v1

import com.wellcome.main.component.FirebaseAuthProvider
import com.wellcome.main.dto.api.newDto.request.v1.ReviewRequestV1
import com.wellcome.main.dto.api.newDto.response.v1.ReviewResponseV1
import com.wellcome.main.dto.api.newDto.response.v1.ReviewActionResponseV1
import com.wellcome.main.dto.api.paths.PathsV1
import com.wellcome.main.dto.api.paths.QueryString
import com.wellcome.main.exception.UnauthorizedException
import com.wellcome.main.service.management.api.ReviewManagementService
import com.wellcome.main.util.functions.getHeader
import com.wellcome.main.util.variables.RequestKey
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = [PathsV1.BASE_REVIEW])
open class ReviewControllerV1 @Autowired constructor(
    private val firebaseAuthProvider: FirebaseAuthProvider,
    private val reviewManagementService: ReviewManagementService
) {

    @PostMapping(value = [PathsV1.Review.SAVE])
    open fun saveReview(@RequestBody request: ReviewRequestV1): ReviewActionResponseV1 {
        val token = getHeader(RequestKey.TOKEN) ?: throw UnauthorizedException("Token is null")
        val initResult = firebaseAuthProvider.getUserCredentialsByFirebaseToken(token)
        return reviewManagementService.saveReview(request.institutionId, request.starCount, request.feedback, initResult.googleUid)
    }

    @GetMapping(value = [PathsV1.Review.GET])
    open fun getReviews(@RequestParam(value = QueryString.INSTITUTION_ID, required = false) institutionId: Long?): ReviewResponseV1 {
        val token = getHeader(RequestKey.TOKEN)
        val initResult = token?.let(firebaseAuthProvider::getUserCredentialsByFirebaseToken)
        return reviewManagementService.getReviews(initResult?.googleUid, institutionId)
    }

}