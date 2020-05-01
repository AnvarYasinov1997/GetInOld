package com.wellcome.main.configuration.utils

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException

@Configuration
open class TelegramBotConfiguration @Autowired constructor(
    @Value(value = "\${telegram-bot-name}") private val botName: String,
    @Value(value = "\${telegram-bot-token}") private val botToken: String,
    @Value(value = "\${telegram-bot-developers-chat-id}") private val botDevelopersChatId: MutableList<String>
) {

    init {
        ApiContextInitializer.init()
    }

    private val bot = DefaultTelegramBotService(botName, botToken, botDevelopersChatId)

    @Bean
    @Qualifier("firstDefault")
    open fun telegramBotInitService(): TelegramBotInit = this.bot

    @Bean
    @Qualifier("firstDefault")
    open fun telegramBotService(): TelegramBotService = this.bot

}

interface TelegramBotInit {
    fun init()
}

interface TelegramBotService {
    fun sendLog(message: String)
}

class DefaultTelegramBotService(
    private val botName: String,
    private val botToken: String,
    private val botDevelopersChatId: MutableList<String>
) : TelegramLongPollingBot(), TelegramBotService, TelegramBotInit {

    override fun init() {
        try {
            TelegramBotsApi().registerBot(this)
        } catch (e: TelegramApiRequestException) {
            e.printStackTrace()
        }
    }

    override fun sendLog(message: String) {
        this.botDevelopersChatId.forEach {
            SendMessage().apply {
                this.enableMarkdown(true)
                this.chatId = it
                this.text = message
            }.let(this::execute)
        }
    }

    override fun onUpdateReceived(update: Update?) {
        if (update != null) {
            SendMessage().also {
                it.enableMarkdown(true)
                it.chatId = update.message.chatId.toString()
                it.text = "Ping"
            }.let(this::execute)
        }
    }

    override fun getBotUsername(): String = this.botName

    override fun getBotToken(): String = this.botToken

}