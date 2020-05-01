package com.wellcome.main.service.management.moderation

import com.wellcome.main.component.TimestampProvider
import com.wellcome.main.dto.moderation.request.v1.BirthdayCampaignAge
import com.wellcome.main.dto.moderation.request.v1.BirthdayCampaignTime
import com.wellcome.main.dto.moderation.request.v1.BirthdayCampaignsRequestV1
import com.wellcome.main.dto.moderation.response.v1.BirthdayCampaignResponseV1
import com.wellcome.main.entity.institution.BirthdayCampaign
import com.wellcome.main.entity.institution.BirthdayCampaignStatus
import com.wellcome.main.entity.user.Gender
import com.wellcome.main.exception.BirthdayCampaignPatternExistException
import com.wellcome.main.service.extentions.generators.api.common.generateBirthdayCampaignDtoV1List
import com.wellcome.main.service.facade.institution.BirthdayCampaignService
import com.wellcome.main.service.facade.institution.InstitutionService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface ModerationBirthdayCampaignsManagementService {
    fun add(request: BirthdayCampaignsRequestV1)
    fun getAll(institutionId: Long): BirthdayCampaignResponseV1
}

@Service
open class DefaultModerationBirthdayCampaignsManagementService constructor(
    private val timestampProvider: TimestampProvider,
    private val institutionService: InstitutionService,
    private val birthdayCampaignService: BirthdayCampaignService
) : ModerationBirthdayCampaignsManagementService {

    @Transactional(readOnly = true)
    override fun getAll(institutionId: Long): BirthdayCampaignResponseV1 {
        return birthdayCampaignService.findByInstitutionId(institutionId)
            .sortedByDescending(BirthdayCampaign::getIdNotNull)
            .generateBirthdayCampaignDtoV1List()
            .let(::BirthdayCampaignResponseV1)

    }

    @Transactional
    override fun add(request: BirthdayCampaignsRequestV1) {
        val institution = institutionService.findById(request.institutionId)

        val birthdayCampaigns =
            birthdayCampaignService.findByInstitutionId(institution.id!!)
                .filterNot { it.status == BirthdayCampaignStatus.DELETED }
                .filterNot { it.status == BirthdayCampaignStatus.EXPIRED }

        val monthCount = when (BirthdayCampaignTime.valueOf(request.expirationTime)) {
            BirthdayCampaignTime.ONE_MONTH -> 1
        }

        val expirationDate = timestampProvider.getUserZonedDateTime()
            .toLocalDate()
            .plusMonths(monthCount.toLong())

        val age = when (BirthdayCampaignAge.valueOf(request.age)) {
            BirthdayCampaignAge.LOW_AGE -> "18-27"
            BirthdayCampaignAge.MIDDLE_AGE -> "28-40"
            BirthdayCampaignAge.HIGH_AGE -> "40-100"
            BirthdayCampaignAge.ALL_AGE -> "all"
        }

        val gender = Gender.valueOf(request.gender)

        fun patternExistException() {
            throw BirthdayCampaignPatternExistException("Campaign width this patter already exist")
        }

        birthdayCampaigns.forEach {
            if (it.gender == Gender.NOT_DETERMINE && it.age == age) patternExistException()
            if (it.age == "all" && it.gender == gender) patternExistException()
            if (it.gender == gender && it.age == age) patternExistException()
            if (it.gender == gender && age == "all") patternExistException()
            if (it.age == age && gender == Gender.NOT_DETERMINE) patternExistException()
            if (age == "all" && gender == Gender.NOT_DETERMINE) patternExistException()
        }

        BirthdayCampaign(
            institution = institution,
            text = request.text,
            age = age,
            gender = gender,
            expirationDate = expirationDate
        ).let(birthdayCampaignService::saveOrUpdate)
    }

}