package com.wellcome.main.controller.web

import com.wellcome.main.dto.api.paths.QueryString
import com.wellcome.main.dto.web.response.AllEventResponse
import com.wellcome.main.dto.web.response.EventResponse
import com.wellcome.main.service.management.web.WebEventManagementService
import com.wellcome.main.util.variables.WebPaths
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping(value = [WebPaths.BASE_SEARCH])
open class WebEventController @Autowired constructor(
    private val webEventManagementService: WebEventManagementService
) {

    @GetMapping(value = [WebPaths.Event.GET])
    open fun getById(@RequestParam(value = QueryString.EVENT_ID) eventId: Long): EventResponse {
        return webEventManagementService.getById(eventId)
    }

    @GetMapping(value = [WebPaths.Event.GET_ALL])
    open fun getAll(): AllEventResponse {
        return webEventManagementService.getAll()
    }

}