package com.wellcome.main.controller.api.newApi.v1

import com.wellcome.main.component.FirebaseAuthProvider
import com.wellcome.main.dto.api.newDto.request.v1.EditAvatarRequestV1
import com.wellcome.main.dto.api.newDto.response.v1.ActionInstitutionResponseV1
import com.wellcome.main.dto.api.newDto.response.v1.EditUserResponseV1
import com.wellcome.main.dto.api.newDto.response.v1.InitResponseV1
import com.wellcome.main.dto.api.newDto.response.v1.UserProfileResponseV1
import com.wellcome.main.dto.api.paths.PathsV1
import com.wellcome.main.dto.api.paths.QueryString
import com.wellcome.main.exception.UnauthorizedException
import com.wellcome.main.service.management.api.UserManagementService
import com.wellcome.main.util.functions.getHeader
import com.wellcome.main.util.functions.getMessage
import com.wellcome.main.util.variables.RequestKey
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import javax.persistence.EntityNotFoundException

@RestController
@RequestMapping(value = [PathsV1.BASE_USER])
open class UserControllerV1 @Autowired constructor(
    private val firebaseAuthProvider: FirebaseAuthProvider,
    private val userManagementService: UserManagementService
) {

    @GetMapping(value = [PathsV1.User.SAVE])
    open fun init(@RequestParam(name = QueryString.INSTANCE_ID) instanceId: String,
             @RequestParam(name = QueryString.FCM_TOKEN) fcmToken: String): InitResponseV1 {
        val token = getHeader(RequestKey.TOKEN)
        return userManagementService.initUser(instanceId, fcmToken, requireNotNull(token))
    }

    @GetMapping(value = [PathsV1.User.CHECK_SESSION])
    open fun checkSession(@RequestParam(value = QueryString.INSTANCE_ID) instanceId: String,
                          @RequestParam(value = QueryString.FCM_TOKEN) fcmToken: String) {
        userManagementService.checkSession(instanceId, fcmToken)
    }

    @GetMapping(value = [PathsV1.User.GET_PROFILE])
    open fun getUserProfile(): UserProfileResponseV1 {
        val token = getHeader(RequestKey.TOKEN) ?: throw UnauthorizedException("token is null")
        val initResult = firebaseAuthProvider.getUserCredentialsByFirebaseToken(token)
        return try {
            userManagementService.getProfileV1(initResult.googleUid)
        } catch (e: EntityNotFoundException) {
            throw UnauthorizedException(e.getMessage())
        }
    }

    @GetMapping(value = [PathsV1.User.SAVE_INSTITUTION])
    open fun saveInstitution(@RequestParam(name = QueryString.INSTITUTION_ID) institutionId: Long): ActionInstitutionResponseV1 {
        val token = getHeader(RequestKey.TOKEN) ?: throw UnauthorizedException("Token is null")
        val initResult = firebaseAuthProvider.getUserCredentialsByFirebaseToken(token)
        try {
            return userManagementService.saveInstitution(initResult.googleUid, institutionId)
        } catch (e: EntityNotFoundException) {
            throw UnauthorizedException(e.getMessage())
        }
    }

    @GetMapping(value = [PathsV1.User.REMOVE_INSTITUTION])
    open fun removeInstitution(@RequestParam(name = QueryString.INSTITUTION_ID) institutionId: Long): ActionInstitutionResponseV1 {
        val token = getHeader(RequestKey.TOKEN)
            ?: throw UnauthorizedException("Not authorized device want remove bookmark")
        val initResult = firebaseAuthProvider.getUserCredentialsByFirebaseToken(token)
        try {
            return userManagementService.removeInstitution(initResult.googleUid, institutionId)
        } catch (e: EntityNotFoundException) {
            throw UnauthorizedException(e.getMessage())
        }
    }

    @PostMapping(value = [PathsV1.User.EDIT_AVATAR])
    open fun editAvatar(@RequestBody request: EditAvatarRequestV1): EditUserResponseV1 {
        val token = getHeader(RequestKey.TOKEN)
            ?: throw UnauthorizedException("Token is null")
        val initResult = firebaseAuthProvider.getUserCredentialsByFirebaseToken(token)
        try {
            return userManagementService.editAvatar(initResult.googleUid, request.avatarUrl)
        } catch (e: EntityNotFoundException) {
            throw UnauthorizedException(e.getMessage())
        }
    }

    @GetMapping(value = [PathsV1.User.EDIT_NAME])
    open fun editName(@RequestParam(value = QueryString.NAME) name: String): EditUserResponseV1 {
        val token = getHeader(RequestKey.TOKEN)
            ?: throw UnauthorizedException("Token is null")
        val initResult = firebaseAuthProvider.getUserCredentialsByFirebaseToken(token)
        try {
            return userManagementService.editName(initResult.googleUid, name)
        } catch (e: EntityNotFoundException) {
            throw UnauthorizedException(e.getMessage())
        }
    }

    @GetMapping(value = [PathsV1.User.EDIT_PUSH_NOTIFICATION])
    open fun editPushNotification(@RequestParam(value = QueryString.PUSH_AVAILABLE) pushAvailable: Boolean): EditUserResponseV1 {
        val token = getHeader(RequestKey.TOKEN)
            ?: throw UnauthorizedException("Token is null")
        val initResult = firebaseAuthProvider.getUserCredentialsByFirebaseToken(token)
        try {
            return userManagementService.editPushNotification(initResult.googleUid, pushAvailable)
        } catch (e: EntityNotFoundException) {
            throw UnauthorizedException(e.getMessage())
        }
    }

    @GetMapping(value = [PathsV1.User.EDIT_DATE_OF_BIRTH])
    open fun editDateOfBirth(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                             @RequestParam(value = QueryString.DATE_OF_BIRTH) dateOfBirth: LocalDate,
                             @RequestParam(value = QueryString.GENDER) gender: String): EditUserResponseV1 {
        val token = getHeader(RequestKey.TOKEN)
            ?: throw UnauthorizedException("Token is null")
        val initResult = firebaseAuthProvider.getUserCredentialsByFirebaseToken(token)
        try {
            return userManagementService.editDateOfBirth(initResult.googleUid, dateOfBirth, gender)
        } catch (e: EntityNotFoundException) {
            throw UnauthorizedException(e.getMessage())
        }
    }

}