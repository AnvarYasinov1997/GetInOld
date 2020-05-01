package com.wellcome.main.service.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.wellcome.main.component.TimestampProvider
import com.wellcome.main.configuration.utils.TelegramBotService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.stereotype.Service
import java.util.logging.Logger

interface LoggerService {
    fun info(logMessage: LogMessage)
    fun warning(logMessage: LogMessage, e: Throwable? = null)
    fun error(logMessage: LogMessage)
    fun sendLogDeveloper(logMessage: LogMessage)
}

@Service
open class DefaultLoggerService @Autowired constructor(
    private val objectMapper: ObjectMapper,
    private val timestampProvider: TimestampProvider,
    private val configurableEnvironment: ConfigurableEnvironment,
    @Qualifier(value = "firstDefault") private val telegramBotService: TelegramBotService
) : LoggerService {
    private val logger = Logger.getAnonymousLogger()

    override fun info(logMessage: LogMessage) {
        logger.info(objectMapper.writeValueAsString(logMessage.message))
    }

    override fun warning(logMessage: LogMessage, e: Throwable?) {
        logger.warning(objectMapper.writeValueAsString(logMessage.message))
        e?.printStackTrace()
    }

    override fun error(logMessage: LogMessage) {
        logger.severe(objectMapper.writeValueAsString(logMessage.message))
    }

    override fun sendLogDeveloper(logMessage: LogMessage) {
        try {
            configurableEnvironment.activeProfiles.firstOrNull("prod"::equals)?.let {
                telegramBotService.sendLog("${logMessage.message}\n - ${timestampProvider.getServerZonedDateTime()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            this.error(LogMessage("Telegram bot service is corrupted"))
        }
    }
}


data class LogMessage(val message: String)