package com.wellcome.main.service.management.api

import com.wellcome.main.service.facade.institution.BirthdayCampaignUserService
import com.wellcome.main.service.facade.user.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityNotFoundException

interface BirthdayCampaignUserManagementService {
    fun expire(googleUid: String, birthdayCampaignUserId: Long)
}

@Service
open class DefaultBirthdayCampaignUserManagementService @Autowired constructor(
    private val userService: UserService,
    private val birthdayCampaignUserService: BirthdayCampaignUserService
) : BirthdayCampaignUserManagementService {

    @Transactional
    override fun expire(googleUid: String, birthdayCampaignUserId: Long) {
        val user = userService.findByGoogleUid(googleUid)
            ?: throw EntityNotFoundException("User width googleUid: $googleUid is not found to database")

        val birthdayCampaignUser = birthdayCampaignUserService.findById(birthdayCampaignUserId)

        if (birthdayCampaignUser.user == user) {
            birthdayCampaignUser.apply {
                this.viewed = true
            }.let(birthdayCampaignUserService::saveOrUpdate)
        } else throw EntityNotFoundException("User width googleUid: $googleUid try delete other campaign")
    }

}