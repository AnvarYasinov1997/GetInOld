package com.wellcome.main.service.scheduler

import com.wellcome.main.component.TimestampProvider
import com.wellcome.main.entity.ApplicationConfigType
import com.wellcome.main.entity.user.Session
import com.wellcome.main.entity.user.User
import com.wellcome.main.service.facade.ApplicationConfigService
import com.wellcome.main.service.facade.LocalityService
import com.wellcome.main.service.facade.user.SessionService
import com.wellcome.main.service.facade.user.UserService
import com.wellcome.main.service.utils.LogMessage
import com.wellcome.main.service.utils.LoggerService
import com.wellcome.main.service.utils.NotificationService
import com.wellcome.main.util.functions.convertToLocalDateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service
open class SessionSchedulerService @Autowired constructor(
    private val userService: UserService,
    private val loggerService: LoggerService,
    private val sessionService: SessionService,
    private val localityService: LocalityService,
    private val timestampProvider: TimestampProvider,
    private val notificationService: NotificationService,
    private val applicationConfigService: ApplicationConfigService
) {

    @Scheduled(fixedDelay = 1000 * 60 * 10)
    open fun checkOldSessions() {
        localityService.findAll().forEach {
            if (it.timezone == "Future") return@forEach
            val localityZonedDateTime = timestampProvider.getUserZonedDateTimeByTimeZoneId(it.timezone)
            val localityLocalDateTime = localityZonedDateTime.toLocalDateTime()
            val lowerRange = "20:00".convertToLocalDateTime(it.timezone)
            val upperRange = "20:11".convertToLocalDateTime(it.timezone)
            if (localityLocalDateTime > lowerRange && localityLocalDateTime < upperRange) {
                userService.findByLocality(requireNotNull(it.id))
                    .filter { user -> user.session != null }
                    .filter(User::pushAvailable)
                    .mapNotNull(User::session)
                    .forEach(this::sendPush)
                loggerService.sendLogDeveloper(
                    LogMessage("Notification service triggered for ${it.name}, at $localityZonedDateTime by ${it.name} time"))
            }
        }
    }

    private inline fun sendPush(session: Session) {
        val oldSessionDay =
            applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.OLD_SESSION_DAY)
                .getLongValueNotNull()
        val oldPushDay =
            applicationConfigService.getConfigValueByConfigType(ApplicationConfigType.OLD_PUSH_NOTIFICATION_DAY)
                .getLongValueNotNull()
        val currentZonedDateTime = timestampProvider.getUserZonedDateTime()
        if (ZonedDateTime.parse(session.latestSession).plusDays(oldSessionDay) < currentZonedDateTime) {
            if (session.latestPushNotification != null) {
                if (ZonedDateTime.parse(session.latestPushNotification).plusDays(oldPushDay) < currentZonedDateTime) {
                    session.apply {
                        this.latestSession = currentZonedDateTime.toString()
                        this.latestPushNotification = currentZonedDateTime.toString()
                    }.let(sessionService::saveOrUpdate)
                    notificationService.sendOldSessionMessage(session.fcmToken)
                }
            } else {
                session.apply {
                    this.latestSession = currentZonedDateTime.toString()
                    this.latestPushNotification = currentZonedDateTime.toString()
                }.let(sessionService::saveOrUpdate)
                notificationService.sendOldSessionMessage(session.fcmToken)
            }
        }
    }

}