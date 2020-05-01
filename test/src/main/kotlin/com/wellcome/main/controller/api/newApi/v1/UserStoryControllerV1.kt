package com.wellcome.main.controller.api.newApi.v1

import com.wellcome.main.component.FirebaseAuthProvider
import com.wellcome.main.dto.api.newDto.request.v1.UserStoryRequestV1
import com.wellcome.main.dto.api.paths.PathsV1
import com.wellcome.main.exception.UnauthorizedException
import com.wellcome.main.service.management.api.UserStoryManagementService
import com.wellcome.main.util.functions.getHeader
import com.wellcome.main.util.variables.RequestKey
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = [PathsV1.BASE_USER_STORY])
open class UserStoryControllerV1 @Autowired constructor(
    private val firebaseAuthProvider: FirebaseAuthProvider,
    private val userStoryManagementService: UserStoryManagementService
) {

    @PostMapping(value = [PathsV1.UserStory.ADD])
    open fun add(@RequestBody request: UserStoryRequestV1) {
        val token = getHeader(RequestKey.TOKEN) ?: throw UnauthorizedException("Token is null")
        val googleUid = firebaseAuthProvider.getUserCredentialsByFirebaseToken(token).googleUid
        userStoryManagementService.add(googleUid, request)
    }

}