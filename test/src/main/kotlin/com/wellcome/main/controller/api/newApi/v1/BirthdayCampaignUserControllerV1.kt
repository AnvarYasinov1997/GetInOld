package com.wellcome.main.controller.api.newApi.v1

import com.wellcome.main.component.FirebaseAuthProvider
import com.wellcome.main.dto.api.paths.PathsV1
import com.wellcome.main.dto.api.paths.QueryString
import com.wellcome.main.exception.UnauthorizedException
import com.wellcome.main.service.management.api.BirthdayCampaignUserManagementService
import com.wellcome.main.util.functions.getHeader
import com.wellcome.main.util.variables.RequestKey
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = [PathsV1.BASE_BIRTHDAY_CAMPAIGN_USER])
open class BirthdayCampaignUserControllerV1 @Autowired constructor(
    private val firebaseAuthProvider: FirebaseAuthProvider,
    private val birthdayCampaignUserManagementService: BirthdayCampaignUserManagementService
){

    @GetMapping(value = [PathsV1.BirthdayCampaignUser.EXPIRE])
    open fun expire(@RequestParam(value = QueryString.BIRTHDAY_CAMPAIGN_USER_ID) birthdayCampaignUserId: Long) {
        val token = getHeader(RequestKey.TOKEN) ?: throw UnauthorizedException("Token is null")
        val initResult = firebaseAuthProvider.getUserCredentialsByFirebaseToken(token)
        birthdayCampaignUserManagementService.expire(initResult.googleUid, birthdayCampaignUserId)
    }
}
