package com.wellcome.main.controller.moderation

import com.wellcome.main.annotations.PreProcessor
import com.wellcome.main.dto.api.paths.QueryString
import com.wellcome.main.dto.moderation.response.v1.*
import com.wellcome.main.exception.UnauthorizedException
import com.wellcome.main.service.management.moderation.ModerationInstitutionProfileManagementService
import com.wellcome.main.util.functions.getProfileContext
import com.wellcome.main.util.variables.ModerationPathsV1
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = [ModerationPathsV1.BASE_INSTITUTION_PROFILE])
open class ModerationInstitutionProfileController @Autowired constructor(
    private val moderationInstitutionProfileManagementService: ModerationInstitutionProfileManagementService
) {

    @GetMapping(value = [ModerationPathsV1.InstitutionProfile.GET_MODERATION_INSTITUTIONS])
    open fun getModerationInstitutions(): ModerationInstitutionResponseV1 {
        val profileContext = getProfileContext() ?: throw UnauthorizedException("Profile context is empty")
        return moderationInstitutionProfileManagementService.getInstitutionProfiles(profileContext.profileModels)
    }

    @PreProcessor
    @GetMapping(value = [ModerationPathsV1.InstitutionProfile.GET_ARCHIVE])
    open fun getArchive(@RequestParam(value = QueryString.INSTITUTION_ID) institutionId: Long): ArchiveResponseV1 {
        return moderationInstitutionProfileManagementService.getArchive(institutionId)
    }

    @PreProcessor
    @GetMapping(value = [ModerationPathsV1.InstitutionProfile.GET_DASHBOARD])
    open fun getDashboard(@RequestParam(value = QueryString.INSTITUTION_ID) institutionId: Long,
                          @RequestParam(value = QueryString.LAST_FETCH_DAY, required = false) lastFetchDate: String?): DashboardResponseV1 {
        return moderationInstitutionProfileManagementService.getDashboard(institutionId, lastFetchDate)
    }

    @PreProcessor
    @GetMapping(value = [ModerationPathsV1.InstitutionProfile.GET_PROFILE_FOR_EDIT])
    open fun getModerationInstitutionProfileForEdit(@RequestParam(value = QueryString.INSTITUTION_ID) institutionId: Long): InstitutionModerationResponseForEditV1 {
        return moderationInstitutionProfileManagementService.getModerationInstitutionProfileForEdit(institutionId)
    }

    @PreProcessor
    @GetMapping(value = [ModerationPathsV1.InstitutionProfile.GET_OFFER_ANALYTIC])
    open fun getOfferAnalytic(@RequestParam(value = QueryString.INSTITUTION_ID) institutionId: Long): OfferAnalyticResponseV1 {
        return moderationInstitutionProfileManagementService.getOfferAnalytic(institutionId)
    }

    @PreProcessor
    @GetMapping(value = [ModerationPathsV1.InstitutionProfile.GET_EVENT_ANALYTIC])
    open fun getEventAnalytic(@RequestParam(value = QueryString.INSTITUTION_ID) institutionId: Long): EventAnalyticResponseV1 {
        return moderationInstitutionProfileManagementService.getEventAnalytic(institutionId)
    }

    @PreProcessor
    @GetMapping(value = [ModerationPathsV1.InstitutionProfile.GET_OLD_OFFER_ANALYTIC])
    open fun getOldOfferAnalytic(@RequestParam(value = QueryString.INSTITUTION_ID) institutionId: Long): OldOfferAnalyticResponseV1 {
        return moderationInstitutionProfileManagementService.getOldOfferAnalytic(institutionId)
    }

    @PreProcessor
    @GetMapping(value = [ModerationPathsV1.InstitutionProfile.GET_OLD_EVENT_ANALYTIC])
    open fun getOldEventAnalytic(@RequestParam(value = QueryString.INSTITUTION_ID) institutionId: Long): OldEventAnalyticResponseV1 {
        return moderationInstitutionProfileManagementService.getOldEventAnalytic(institutionId)
    }

    @GetMapping(value=[ModerationPathsV1.InstitutionProfile.GET_MARKETING_CONTENT])
    open fun getMarketingContent(@RequestParam(value = QueryString.MARKETING_ID) marketingId: Long): MarketingResponseV1 {
        return moderationInstitutionProfileManagementService.getMarketingContent(marketingId)
    }

    @GetMapping(value = [ModerationPathsV1.InstitutionProfile.GET_INSTRUCTION_CONTENT])
    open fun getInstructionContent(@RequestParam(value = QueryString.INSTITUTION_ID) instructionId: Long): InstructionResponseV1 {
        return moderationInstitutionProfileManagementService.getInstructionContent(instructionId)
    }

}