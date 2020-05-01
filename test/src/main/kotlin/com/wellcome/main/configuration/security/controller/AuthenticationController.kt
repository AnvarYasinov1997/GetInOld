package com.wellcome.main.configuration.security.controller

import com.wellcome.main.component.FirebaseAuthProvider
import com.wellcome.main.configuration.security.dto.AuthorizeInstitutionRequestV1
import com.wellcome.main.configuration.security.dto.AuthorizeInstitutionResponseV1
import com.wellcome.main.configuration.security.dto.PairTokenDto
import com.wellcome.main.configuration.security.service.AuthenticationManagementService
import com.wellcome.main.dto.InitResponse
import com.wellcome.main.exception.UnauthorizedException
import com.wellcome.main.util.functions.getHeader
import com.wellcome.main.util.variables.Paths
import com.wellcome.main.util.variables.Query
import com.wellcome.main.util.variables.RequestKey
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.io.IOException

@RestController
@RequestMapping(value = [Paths.BASE_AUTH])
open class AuthenticationController @Autowired constructor(
    private val firebaseAuthProvider: FirebaseAuthProvider,
    private val authenticationManagementService: AuthenticationManagementService
) {

    @GetMapping(value = [Paths.Auth.INIT_USER])
    open fun init(@RequestParam(name = Query.LAT) lat: Double,
                  @RequestParam(name = Query.LON) lon: Double): InitResponse {
        val token = getHeader(RequestKey.TOKEN)
        return authenticationManagementService.init(lat, lon, requireNotNull(token))
    }

    @Throws(IOException::class)
    @PostMapping(value = [Paths.Auth.TOKEN], produces = [MediaType.APPLICATION_JSON_VALUE])
    open fun refreshToken(@RequestBody body: String): PairTokenDto {
        return authenticationManagementService.refreshToken(body)
    }

    @PostMapping(value = [Paths.Auth.AUTHORIZE_INSTITUTION])
    open fun authorizeInstitution(@RequestBody request: AuthorizeInstitutionRequestV1): AuthorizeInstitutionResponseV1 {
        val token = getHeader(RequestKey.TOKEN) ?: throw UnauthorizedException("token is null")
        val initResult = firebaseAuthProvider.getUserCredentialsByFirebaseToken(token)
        val moderationJwtToken = getHeader(RequestKey.MODERATION_JWT_TOKEN).let { if (it == null || it.isEmpty()) null else it }
        return authenticationManagementService.authorizeInstitution(initResult.googleUid, request.login, request.password, moderationJwtToken)
    }

}
