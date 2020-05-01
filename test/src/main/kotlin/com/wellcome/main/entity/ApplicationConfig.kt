package com.wellcome.main.entity

import javax.persistence.*

@Entity
@Table(name = "application_config")
class ApplicationConfig(

    @Column(name = "config_type", nullable = false, unique = true)
    @Enumerated(value = EnumType.STRING)
    var configType: ApplicationConfigType,

    @Column(name = "long_value")
    var longValue: Long?,

    @Column(name = "double_value")
    var doubleValue: Double?,

    @Column(name = "string_value")
    var stringValue: String?

) : BaseEntity() {

    fun toConfigValue(): ConfigValue {
        return ConfigValue(
            longValue = this.longValue,
            doubleValue = this.doubleValue,
            stringValue = this.stringValue
        )
    }

}

data class ConfigValue(val longValue: Long?,
                       val doubleValue: Double?,
                       val stringValue: String?) {
    fun getLongValueNotNull(): Long = requireNotNull(this.longValue)
    fun getDoubleValueNotNull(): Double = requireNotNull(this.doubleValue)
    fun getStringValueNotNull(): String = requireNotNull(this.stringValue)
}

enum class ApplicationConfigType(val defaultValue: ConfigValue) {
    GRAB_SEARCH_RADIUS(ConfigValue(1000L, null, null)),
    SEARCH_RADIUS(ConfigValue(300L, null, null)),
    RECOMMENDATION_SEARCH_RADIUS(ConfigValue(1000L, null, null)),
    PICTURE_RESOLUTION(ConfigValue(500L, null, null)),
    EXPIRATION_TIME(ConfigValue(120L, null, null)),
    PAGINATION_END_ELEMENT(ConfigValue(20L, null, null)),
    BASE_MODERATION_URL(ConfigValue(null, null, ApplicationConfigType.BASE_MODERATION_URL_VALUE)),
    CUSTOMER_MESSAGE(ConfigValue(null, null, ApplicationConfigType.CUSTOMER_MESSAGE_VALUE)),
    FIRST_QUESTION_TITLE(ConfigValue(null, null, ApplicationConfigType.FIRST_QUESTION_TITLE_VALUE)),
    SECOND_QUESTION_TITLE(ConfigValue(null, null, ApplicationConfigType.SECOND_QUESTION_TITLE_VALUE)),
    RECOMMENDED_PLACES_TITLE(ConfigValue(null, null, ApplicationConfigType.RECOMMENDED_PLACES_TITLE_VALUE)),
    RECOMMENDED_OFFERS_TITLE(ConfigValue(null, null, ApplicationConfigType.RECOMMENDED_OFFERS_TITLE_VALUE)),
    RECOMMENDED_OFFERS_RESULT_TITLE(ConfigValue(null, null, ApplicationConfigType.RECOMMENDED_OFFERS_RESULT_TITLE_VALUE)),
    RECOMMENDED_PLACES_RESULT_TITLE(ConfigValue(null, null, ApplicationConfigType.RECOMMENDED_PLACES_RESULT_TITLE_VALUE)),
    OTHER_OFFERS_TITLE(ConfigValue(null, null, ApplicationConfigType.OTHER_OFFERS_TITLE_VALUE)),
    OTHER_OFFERS_TITLE_ALL(ConfigValue(null, null, ApplicationConfigType.OTHER_OFFERS_TITLE_VALUE_ALL_VALUE)),
    OTHER_PLACES_TITLE(ConfigValue(null, null, ApplicationConfigType.OTHER_PLACES_TITLE_VALUE)),
    EVENT_FIRST_TITLE(ConfigValue(null, null, ApplicationConfigType.EVENT_FIRST_TITLE_VALUE)),
    EVENT_SECOND_TITLE(ConfigValue(null, null, ApplicationConfigType.EVENT_SECOND_TITLE_VALUE)),
    OLD_SESSION_NOTIFICATION_MESSAGE(ConfigValue(null, null, ApplicationConfigType.OLD_SESSION_NOTIFICATION_MESSAGE_VALUE)),
    OLD_SESSION_NOTIFICATION_TITLE(ConfigValue(null, null, ApplicationConfigType.OLD_SESSION_NOTIFICATION_TITLE_VALUE)),
    OLD_SESSION_DAY(ConfigValue(14, null, null)),
    OLD_PUSH_NOTIFICATION_DAY(ConfigValue(5, null, null)),
    TODAY_EVENTS_TITLE(ConfigValue(null, null, ApplicationConfigType.TODAY_EVENTS_TITLE_VALUE)),
    CATEGORY_INSTITUTION_EVENTS(ConfigValue(null, null, ApplicationConfigType.CATEGORY_INSTITUTION_EVENTS_VALUE)),
    OFFER_RANGING_SEARCH_ATTRIBUTES_SCREEN_COUNT(ConfigValue(3, null, null)),
    INSTITUTION_RANGING_SEARCH_ATTRIBUTES_SCREEN_COUNT(ConfigValue(3, null, null)),
    OFFER_RANGING_SEARCH_SCREEN_COUNT(ConfigValue(3, null, null)),
    INSTITUTION_RANGING_SEARCH_SCREEN_COUNT(ConfigValue(3, null, null)),
    OFFER_RANGING_OFFER_SCREEN_COUNT(ConfigValue(3, null, null)),
    INSTITUTION_OFFERS_TITLE(ConfigValue(null, null, ApplicationConfigType.INSTITUTION_OFFERS_TITLE_VALUE)),
    INSTITUTION_PROFILE_REVIEWS_COUNT(ConfigValue(3, null, null)),
    INSTITUTION_PROFILE_OFFERS_COUNT(ConfigValue(3, null, null)),
    USER_PROFILE_REVIEWS_TITLE(ConfigValue(null, null, ApplicationConfigType.USER_PROFILE_REVIEWS_TITLE)),
    CLOSEST_INSTITUTION_TITLE(ConfigValue(2, null, ApplicationConfigType.CLOSEST_INSTITUTION_TITLE)),
    EVENT_RANGING_SEARCH_ATTRIBUTES_SCREEN_COUNT(ConfigValue(5, null, null)),
    STORY_RANGING_SEARCH_ATTRIBUTES_COUNT(ConfigValue(3,null, null)),
    BIRTHDAY_CAMPAIGN_MESSAGE_TITLE(ConfigValue(null,null, "")),
    BIRTHDAY_CAMPAIGN_MESSAGE_TEXT(ConfigValue(null,null, ""));

    companion object {
        private const val CUSTOMER_MESSAGE_VALUE = "Дороу))) вот ссылка:"
        private const val BASE_MODERATION_URL_VALUE = "http://35.238.70.65:8080/customize?token="
        private const val FIRST_QUESTION_TITLE_VALUE = "Я хочу"
        private const val SECOND_QUESTION_TITLE_VALUE = "Когда"
        private const val RECOMMENDED_PLACES_TITLE_VALUE = "Советуем вам сходить"
        private const val RECOMMENDED_OFFERS_TITLE_VALUE = "Рекомендованные акции"
        private const val RECOMMENDED_OFFERS_RESULT_TITLE_VALUE = "Акции"
        private const val RECOMMENDED_PLACES_RESULT_TITLE_VALUE = "Рекомендуем"
        private const val OTHER_OFFERS_TITLE_VALUE = "Может быть интересно"
        private const val OTHER_OFFERS_TITLE_VALUE_ALL_VALUE = "Все акции прямо сейчас"
        private const val OTHER_PLACES_TITLE_VALUE = "Остальные"
        private const val EVENT_FIRST_TITLE_VALUE = "Текущие"
        private const val EVENT_SECOND_TITLE_VALUE = "Ожидаемые"
        private const val OLD_SESSION_NOTIFICATION_MESSAGE_VALUE = "Пора зайти в Get-in"
        private const val OLD_SESSION_NOTIFICATION_TITLE_VALUE = "Пора зайти в Get-in"
        private const val TODAY_EVENTS_TITLE_VALUE = "Афиша на сегодня"
        private const val CATEGORY_INSTITUTION_EVENTS_VALUE = "Афиша"
        private const val INSTITUTION_OFFERS_TITLE_VALUE = "Все акции в"
        private const val USER_PROFILE_REVIEWS_TITLE = "Все отзывы в "
        private const val CLOSEST_INSTITUTION_TITLE = "Ближайшие"
    }
}