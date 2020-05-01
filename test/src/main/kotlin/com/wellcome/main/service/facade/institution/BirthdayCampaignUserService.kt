package com.wellcome.main.service.facade.institution

import com.wellcome.main.entity.institution.BirthdayCampaignUser
import com.wellcome.main.repository.local.postgre.BirthdayCampaignUserRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface BirthdayCampaignUserService : BaseService<BirthdayCampaignUser> {
    fun findByUser(userId: Long): BirthdayCampaignUser?
    fun findAllNotExpire(): List<BirthdayCampaignUser>
}

@Service
open class DefaultBirthdayCampaignUserService @Autowired constructor(
    private val birthdayCampaignUserRepository: BirthdayCampaignUserRepository
) : DefaultBaseService<BirthdayCampaignUser>(BirthdayCampaignUser::class.java.simpleName, birthdayCampaignUserRepository),
    BirthdayCampaignUserService {

    @Transactional(readOnly = true)
    override fun findByUser(userId: Long): BirthdayCampaignUser? =
        birthdayCampaignUserRepository.findByUserIdAndExpiredFalse(userId)
            .firstOrNull()

    @Transactional(readOnly = true)
    override fun findAllNotExpire(): List<BirthdayCampaignUser> =
        birthdayCampaignUserRepository.findByExpiredFalse()

}