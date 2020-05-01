package com.wellcome.main.service.scheduler

import com.wellcome.main.component.TimestampProvider
import com.wellcome.main.entity.institution.BirthdayCampaignStatus
import com.wellcome.main.entity.institution.BirthdayCampaignUser
import com.wellcome.main.entity.user.Gender
import com.wellcome.main.service.facade.LocalityService
import com.wellcome.main.service.facade.institution.BirthdayCampaignService
import com.wellcome.main.service.facade.institution.BirthdayCampaignUserService
import com.wellcome.main.service.facade.user.UserService
import com.wellcome.main.service.utils.NotificationService
import com.wellcome.main.util.functions.convertToLocalDateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.ZonedDateTime

@Service
open class BirthdayCampaignsSchedulerService @Autowired constructor(
    private val userService: UserService,
    private val localityService: LocalityService,
    private val timestampProvider: TimestampProvider,
    private val notificationService: NotificationService,
    private val birthdayCampaignService: BirthdayCampaignService,
    private val birthdayCampaignUserService: BirthdayCampaignUserService
) {

    @Scheduled(fixedDelay = 1000 * 60 * 10)
    open fun checkBirthdayCampaignsStart() {
        val campaigns = birthdayCampaignService.findAll().filter {
            it.status == BirthdayCampaignStatus.ACTIVE
        }
        localityService.findAll().forEach { locality ->
            if (locality.timezone == "Future") return@forEach
            val localityZonedDateTime = timestampProvider.getUserZonedDateTimeByTimeZoneId(locality.timezone)
            val localityLocalDateTime = localityZonedDateTime.toLocalDateTime()
            val lowerRange = "19:00".convertToLocalDateTime(locality.timezone)
            val upperRange = "19:11".convertToLocalDateTime(locality.timezone)
            if (localityLocalDateTime > lowerRange && localityLocalDateTime < upperRange) {
                userService.findByLocality(locality.id!!)
                    .filter { user -> user.dateOfBirth != null }
                    .filter { user -> user.gender != Gender.NOT_DETERMINE }
                    .filter { user ->
                        user.dateOfBirth!!.dayOfYear - localityLocalDateTime.toLocalDate().dayOfYear == 7
                    }
                    .forEach { user ->
                        val age = this.getAge(localityLocalDateTime.toLocalDate(), user.dateOfBirth!!)
                        val birthdayCampaignUser =
                            birthdayCampaignUserService.findByUser(user.id!!) ?: BirthdayCampaignUser(user = user)
                        if (!birthdayCampaignUser.viewed) {
                            campaigns.forEach { campaign ->
                                if (campaign.age == "all") {
                                    birthdayCampaignUser.birthdayCampaigns.add(campaign)
                                } else {
                                    val ageRange = campaign.age.split("-").map(String::toInt)
                                    if (age in IntRange(ageRange[0], ageRange[1]) && user.gender == campaign.gender) {
                                        birthdayCampaignUser.birthdayCampaigns.add(campaign)
                                    }
                                }
                            }
                            if (birthdayCampaignUser.birthdayCampaigns.isNotEmpty()) {
                                user.session?.fcmToken?.let(notificationService::sendBirthdayMessage)
                                birthdayCampaignUserService.saveOrUpdate(birthdayCampaignUser)
                            }
                        }
                    }
            }
        }
    }

    @Scheduled(cron = "0 1 0 * * *")
    open fun checkBirthdayCampaignsUserEnd() {
        val localityLocalDate = timestampProvider.getUserZonedDateTime().toLocalDate()
        birthdayCampaignUserService.findAllNotExpire().forEach { campaignsUser ->
            val daysBeforeBirthday =
                requireNotNull(campaignsUser.user.dateOfBirth).dayOfYear -
                    localityLocalDate.dayOfYear
            if (daysBeforeBirthday <= 0) {
                campaignsUser.apply {
                    this.expired = true
                }.let(birthdayCampaignUserService::saveOrUpdate)
            }
        }
    }

    @Scheduled(cron = "0 1 0 * * *")
    open fun checkBirthdayCampaignsEnd() {
        birthdayCampaignService.findAll().forEach {
            if (it.expirationDate < timestampProvider.getServerZonedDateTime().toLocalDate()) {
                it.apply {
                    this.status = BirthdayCampaignStatus.EXPIRED
                }.let(birthdayCampaignService::saveOrUpdate)
            }
        }
    }

    private fun getAge(currentDate: LocalDate, dateOfBirth: LocalDate): Int {
        val yearDifference = currentDate.year - dateOfBirth.year
        return if (currentDate.month < dateOfBirth.month) {
            yearDifference - 1
        } else if (currentDate.month == dateOfBirth.month) {
            if (currentDate.dayOfMonth < dateOfBirth.dayOfMonth) {
                yearDifference - 1
            } else yearDifference
        } else yearDifference
    }

}