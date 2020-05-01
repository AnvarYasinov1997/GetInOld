package com.wellcome.main.controller.api.newApi.v1

import com.wellcome.main.component.FirebaseAuthProvider
import com.wellcome.main.dto.api.newDto.request.v1.FullBlockOfferRequestV1
import com.wellcome.main.dto.api.newDto.response.v1.FullOfferResponseV1
import com.wellcome.main.dto.api.newDto.response.v1.OfferResponseV1
import com.wellcome.main.dto.api.paths.PathsV1
import com.wellcome.main.service.management.api.OfferManagementService
import com.wellcome.main.util.functions.getHeader
import com.wellcome.main.util.variables.RequestKey
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = [PathsV1.BASE_OFFER])
open class OfferControllerV1 @Autowired constructor(
    private val firebaseAuthProvider: FirebaseAuthProvider,
    private val offerManagementService: OfferManagementService
) {

    @GetMapping(value = [PathsV1.Offers.GET])
    open fun getOffers(): OfferResponseV1 {
        val token = getHeader(RequestKey.TOKEN)
        val googleUid = token?.let(firebaseAuthProvider::getUserCredentialsByFirebaseToken)?.googleUid
        val reviewing = getHeader(RequestKey.REVIEWING)?.toBoolean() ?: throw Exception("Require review flag")
        return offerManagementService.getOffersV1(reviewing, googleUid, 5)
    }

    @PostMapping(value = [PathsV1.Offers.GET_FULL_BLOCK])
    open fun getFullBlockOffers(@RequestBody request: FullBlockOfferRequestV1): FullOfferResponseV1 {
        val token = getHeader(RequestKey.TOKEN)
        val googleUid = token?.let(firebaseAuthProvider::getUserCredentialsByFirebaseToken)?.googleUid
        val reviewing = getHeader(RequestKey.REVIEWING)?.toBoolean() ?: throw Exception("Require review flag")
        return offerManagementService.getFullBlockOffersV1(reviewing, googleUid, 5, request)
    }

}