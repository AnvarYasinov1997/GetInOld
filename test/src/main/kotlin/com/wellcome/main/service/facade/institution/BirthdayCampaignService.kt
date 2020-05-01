package com.wellcome.main.service.facade.institution

import com.wellcome.main.entity.institution.BirthdayCampaign
import com.wellcome.main.repository.local.postgre.BirthdayCampaignRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface BirthdayCampaignService : BaseService<BirthdayCampaign> {
    fun findByInstitutionId(institutionId: Long): List<BirthdayCampaign>
}

@Service
open class DefaultBirthdayCampaignService @Autowired constructor(
    private val birthdayCampaignRepository: BirthdayCampaignRepository
) : DefaultBaseService<BirthdayCampaign>(BirthdayCampaign::class.java.simpleName, birthdayCampaignRepository),
    BirthdayCampaignService {

    @Transactional(readOnly = true)
    override fun findByInstitutionId(institutionId: Long): List<BirthdayCampaign> =
        birthdayCampaignRepository.findByInstitutionId(institutionId)

}