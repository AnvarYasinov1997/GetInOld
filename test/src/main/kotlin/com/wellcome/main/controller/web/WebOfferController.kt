package com.wellcome.main.controller.web

import com.wellcome.main.component.FirebaseAuthProvider
import com.wellcome.main.dto.web.request.AllOfferRequest
import com.wellcome.main.dto.web.response.AllOfferResponse
import com.wellcome.main.service.management.web.WebOfferManagementService
import com.wellcome.main.util.functions.getHeader
import com.wellcome.main.util.variables.RequestKey
import com.wellcome.main.util.variables.WebPaths
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = [WebPaths.BASE_OFFER])
open class WebOfferController @Autowired constructor(
    private val firebaseAuthProvider: FirebaseAuthProvider,
    private val webOfferManagementService: WebOfferManagementService
) {

    @GetMapping(value = [WebPaths.Offer.GET_ALL])
    open fun getAll(@RequestBody request: AllOfferRequest): AllOfferResponse {
        val token = getHeader(RequestKey.TOKEN)
        val googleUid = token?.let(firebaseAuthProvider::getUserCredentialsByFirebaseToken)?.googleUid
        return webOfferManagementService.getAll(googleUid, 5, request)
    }

}