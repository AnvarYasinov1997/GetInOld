package com.wellcome.main.service.utils

import com.google.cloud.translate.Translate
import com.google.cloud.translate.Translate.TranslateOption.sourceLanguage
import com.google.cloud.translate.Translate.TranslateOption.targetLanguage
import com.wellcome.main.exception.LanguageUnsupportedException
import com.wellcome.main.util.enumerators.Languages
import com.wellcome.main.util.functions.ifNotEmpty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

interface TranslationService {
    fun translateAny(text: String): String
    fun russianToEnglish(text: String): String
    fun englishToRussian(text: String): String
}

@Service
open class GoogleCloudTranslationService @Autowired constructor(
    private val translate: Translate
) : TranslationService {

    override fun translateAny(text: String): String {
//        return text.toCharArray().toList().ifNotEmpty()?.first()?.checkLang()?.translate(text)
//            ?: throw Exception("Text is empty")
        return text
    }

    override fun russianToEnglish(text: String): String =
        translate.translate(text, sourceLanguage(Languages.RUSSIAN.lang), targetLanguage(Languages.ENGLISH.lang)).translatedText

    override fun englishToRussian(text: String): String =
        translate.translate(text, sourceLanguage(Languages.ENGLISH.lang), targetLanguage(Languages.RUSSIAN.lang)).translatedText

    private fun Char.checkLang(): Languages {
        if (this in 'a'..'z' || this in 'A'..'Z') return Languages.ENGLISH
        if (this in 'а'..'я' || this in 'А'..'Я') return Languages.RUSSIAN
        throw LanguageUnsupportedException("Lang width latter $this is not supported")
    }

    private fun Languages.translate(text: String): String {
        return when (this) {
            Languages.RUSSIAN -> russianToEnglish(text)
            Languages.ENGLISH -> englishToRussian(text)
        }
    }

}