package com.wellcome.main.controller.moderation

import com.wellcome.main.annotations.PreProcessor
import com.wellcome.main.dto.api.paths.QueryString
import com.wellcome.main.dto.moderation.request.v1.BirthdayCampaignsRequestV1
import com.wellcome.main.dto.moderation.response.v1.BirthdayCampaignResponseV1
import com.wellcome.main.service.management.moderation.ModerationBirthdayCampaignsManagementService
import com.wellcome.main.util.variables.ModerationPathsV1
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = [ModerationPathsV1.BASE_BIRTHDAY_CAMPAIGNS])
open class ModerationBirthdayCampaignsController @Autowired constructor(
    private val moderationBirthdayCampaignsManagementService: ModerationBirthdayCampaignsManagementService
) {

    @PreProcessor
    @PostMapping(value = [ModerationPathsV1.BirthdayCampaigns.ADD])
    open fun add(@RequestBody request: BirthdayCampaignsRequestV1) {
        moderationBirthdayCampaignsManagementService.add(request)
    }

    @PreProcessor
    @GetMapping(value = [ModerationPathsV1.BirthdayCampaigns.GET_ALL])
    open fun getAll(@RequestParam(value = QueryString.INSTITUTION_ID) institutionId: Long): BirthdayCampaignResponseV1 {
        return moderationBirthdayCampaignsManagementService.getAll(institutionId)
    }

}