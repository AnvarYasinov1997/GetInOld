package com.wellcome.main.controller.admin

import com.wellcome.main.dto.admin.request.MarketingRequest
import com.wellcome.main.dto.admin.response.MarketingResponse
import com.wellcome.main.service.management.admin.AdminMarketingManagementService
import com.wellcome.main.util.variables.Paths
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = [Paths.BASE_MARKETING])
open class AdminMarketingController @Autowired constructor(
    private val adminMarketingManagementService: AdminMarketingManagementService
) {

    @PostMapping(value = [Paths.Marketing.ADD])
    open fun add(@RequestBody request: MarketingRequest) {
        adminMarketingManagementService.add(request)
    }

    @GetMapping(value = [Paths.Marketing.GET_ALL])
    open fun getAll(): MarketingResponse {
        return adminMarketingManagementService.getAll()
    }

}