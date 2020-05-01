package com.wellcome.main.service.utils

import com.google.firebase.messaging.Notification
import com.wellcome.main.entity.ApplicationConfigType
import com.wellcome.main.repository.remote.notification.NotificationRepository
import com.wellcome.main.service.facade.ApplicationConfigService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.stereotype.Service

interface NotificationService {
    fun sendBirthdayMessage(token: String)
    fun sendOldSessionMessage(token: String)
}

@Service
open class DefaultNotificationService @Autowired constructor(
    private val configurableEnvironment: ConfigurableEnvironment,
    private val notificationRepository: NotificationRepository,
    private val applicationConfigService: ApplicationConfigService
) : NotificationService {

    override fun sendBirthdayMessage(token: String) {
        val title = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.BIRTHDAY_CAMPAIGN_MESSAGE_TITLE).getStringValueNotNull()
        val message = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.BIRTHDAY_CAMPAIGN_MESSAGE_TEXT).getStringValueNotNull()
        this.sendNotification(token, title, message)
    }

    override fun sendOldSessionMessage(token: String) {
        val title = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.OLD_SESSION_NOTIFICATION_TITLE).getStringValueNotNull()
        val message = applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.OLD_SESSION_NOTIFICATION_MESSAGE).getStringValueNotNull()
        this.sendNotification(token, title, message)
    }

    private fun sendNotification(token: String, title: String, message: String) {
        configurableEnvironment.activeProfiles.firstOrNull("prod"::equals)?.let {
            notificationRepository.sendDirectNotification(token, Notification(title, message))
        }
    }

}