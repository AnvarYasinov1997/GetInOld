package com.wellcome.main.controller.api.newApi.v1

import com.wellcome.main.component.FirebaseAuthProvider
import com.wellcome.main.dto.api.newDto.response.v1.EventResponseV1
import com.wellcome.main.dto.api.paths.PathsV1
import com.wellcome.main.service.management.api.EventManagementService
import com.wellcome.main.util.functions.getHeader
import com.wellcome.main.util.variables.RequestKey
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = [PathsV1.BASE_EVENT])
open class EventControllerV1 @Autowired constructor(
    private val firebaseAuthProvider: FirebaseAuthProvider,
    private val eventManagementService: EventManagementService
) {

    @GetMapping(value = [PathsV1.Event.GET])
    open fun getEvents(): EventResponseV1 {
        val token = getHeader(RequestKey.TOKEN)
        val googleUid = token?.let(firebaseAuthProvider::getUserCredentialsByFirebaseToken)?.googleUid
        val reviewing = getHeader(RequestKey.REVIEWING)?.toBoolean() ?: throw Exception("Require review flag")
        return eventManagementService.getEvents(reviewing, 5, googleUid)
    }

}

