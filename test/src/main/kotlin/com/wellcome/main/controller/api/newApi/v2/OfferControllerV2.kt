package com.wellcome.main.controller.api.newApi.v2

import com.wellcome.main.component.FirebaseAuthProvider
import com.wellcome.main.dto.api.newDto.request.v1.FullBlockOfferRequestV1
import com.wellcome.main.dto.api.newDto.response.v2.FullOfferResponseV2
import com.wellcome.main.dto.api.paths.PathsV2
import com.wellcome.main.service.management.api.OfferManagementService
import com.wellcome.main.util.functions.getHeader
import com.wellcome.main.util.variables.RequestKey
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = [PathsV2.BASE_OFFER])
open class OfferControllerV2 @Autowired constructor(
    private val firebaseAuthProvider: FirebaseAuthProvider,
    private val offerManagementService: OfferManagementService
) {

    @PostMapping(value = [PathsV2.Offer.GET_FULL_BLOCK])
    open fun getFullBlockOffers(@RequestBody request: FullBlockOfferRequestV1): FullOfferResponseV2 {
        val token = getHeader(RequestKey.TOKEN)
        val googleUid = token?.let(firebaseAuthProvider::getUserCredentialsByFirebaseToken)?.googleUid
        val reviewing = getHeader(RequestKey.REVIEWING)?.toBoolean() ?: throw Exception("Require review flag")
        return offerManagementService.getFullBlockOffersV2(reviewing, googleUid, 5, request)
    }

}