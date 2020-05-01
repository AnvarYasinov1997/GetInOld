package com.wellcome.main.repository.remote.notification

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import com.wellcome.main.service.utils.LogMessage
import com.wellcome.main.service.utils.LoggerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

interface NotificationRepository {
    fun sendDirectNotification(token: String, notification: Notification)
    fun sendTopicNotification(topic: String, notification: Notification)
}

@Repository
open class DefaultNotificationRepository @Autowired constructor(
//    private val app: FirebaseApp,
    private val logger: LoggerService
) : NotificationRepository {

    override fun sendDirectNotification(token: String, notification: Notification) {
        val message = Message.builder()
            .setToken(token)
            .setNotification(notification)
            .build()
        try {
            val response = FirebaseMessaging.getInstance().send(message)
            logger.info(LogMessage(response))
        } catch (e: FirebaseMessagingException) {
            e.printStackTrace()
            logger.error(LogMessage("Unable to send direct $e"))
        }
    }

    override fun sendTopicNotification(topic: String, notification: Notification) {
        val message = Message.builder()
            .setTopic(topic)
            .setNotification(notification)
            .build()
        try {
            val response = FirebaseMessaging.getInstance().send(message)
            logger.info(LogMessage(response))

        } catch (e: FirebaseMessagingException) {
            e.printStackTrace()
            logger.error(LogMessage("Unable to send topic notification $e"))
        }
    }

}
