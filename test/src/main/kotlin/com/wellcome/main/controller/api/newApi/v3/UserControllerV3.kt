package com.wellcome.main.controller.api.newApi.v3

import com.wellcome.main.component.FirebaseAuthProvider
import com.wellcome.main.dto.api.newDto.response.v3.UserProfileResponseV3
import com.wellcome.main.dto.api.paths.PathsV3
import com.wellcome.main.exception.UnauthorizedException
import com.wellcome.main.service.management.api.UserManagementService
import com.wellcome.main.util.functions.getHeader
import com.wellcome.main.util.functions.getMessage
import com.wellcome.main.util.variables.RequestKey
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.persistence.EntityNotFoundException

@RestController
@RequestMapping(value = [PathsV3.BASE_USER])
open class UserControllerV3 @Autowired constructor(
    private val firebaseAuthProvider: FirebaseAuthProvider,
    private val userManagementService: UserManagementService
) {

    @GetMapping(value = [PathsV3.User.GET_PROFILE])
    open fun getProfile(): UserProfileResponseV3 {
        val token = getHeader(RequestKey.TOKEN) ?: throw UnauthorizedException("token is null")
        val initResult = firebaseAuthProvider.getUserCredentialsByFirebaseToken(token)
        return try {
            userManagementService.getProfileV3(initResult.googleUid)
        } catch (e: EntityNotFoundException) {
            throw UnauthorizedException(e.getMessage())
        }
    }

}