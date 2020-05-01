package com.wellcome.main.controller.api.newApi.v1

import com.wellcome.main.component.FirebaseAuthProvider
import com.wellcome.main.dto.api.newDto.response.v1.LikeResponseV1
import com.wellcome.main.dto.api.newDto.response.v1.StoryResponseV1
import com.wellcome.main.dto.api.paths.PathsV1
import com.wellcome.main.dto.api.paths.QueryString
import com.wellcome.main.exception.UnauthorizedException
import com.wellcome.main.service.management.api.StoryManagementService
import com.wellcome.main.util.functions.getHeader
import com.wellcome.main.util.variables.RequestKey
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = [PathsV1.BASE_STORY])
open class StoryControllerV1 @Autowired constructor(
    private val firebaseAuthProvider: FirebaseAuthProvider,
    private val storyManagementService: StoryManagementService
) {

    @GetMapping(value = [PathsV1.Story.GET_ALL])
    open fun getStories(@RequestParam(value = QueryString.STORY_TYPE) storyType: String): StoryResponseV1 {
        val reviewing = getHeader(RequestKey.REVIEWING)?.toBoolean() ?: throw Exception("Require review flag")
        val token = getHeader(RequestKey.TOKEN)
        val googleUid = token?.let { firebaseAuthProvider.getUserCredentialsByFirebaseToken(token) }?.googleUid
        return storyManagementService.getAll(googleUid, reviewing, storyType)
    }

    @GetMapping(value = [PathsV1.Story.LIKE])
    open fun like(@RequestParam(value = QueryString.STORY_ID) storyId: Long): LikeResponseV1 {
        val token = getHeader(RequestKey.TOKEN) ?: throw UnauthorizedException("token is null")
        val initResult = firebaseAuthProvider.getUserCredentialsByFirebaseToken(token)
        return storyManagementService.like(initResult.googleUid, storyId)
    }

    @GetMapping(value = [PathsV1.Story.DISLIKE])
    open fun dislike(@RequestParam(value = QueryString.STORY_ID) storyId: Long): LikeResponseV1 {
        val token = getHeader(RequestKey.TOKEN) ?: throw UnauthorizedException("token is null")
        val initResult = firebaseAuthProvider.getUserCredentialsByFirebaseToken(token)
        return storyManagementService.dislike(initResult.googleUid, storyId)
    }

    @GetMapping(value = [PathsV1.Story.NOT_INTERESTING])
    open fun notInteresting(@RequestParam(value = QueryString.STORY_ID) storyId: Long): LikeResponseV1 {
        val token = getHeader(RequestKey.TOKEN) ?: throw UnauthorizedException("token is null")
        val initResult = firebaseAuthProvider.getUserCredentialsByFirebaseToken(token)
        return storyManagementService.notInteresting(initResult.googleUid, storyId)
    }

}