package com.wellcome.main.dto.api.newDto

enum class Days {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY,
    NOW,
    TODAY,
    TOMORROW,
    DAY_AFTER_TOMORROW,
    BIRTHDAY;

    fun toAllTitle(): String {
        return when(this) {
            MONDAY -> "Все акции в понедельник"
            TUESDAY -> "Все акции во вторник"
            WEDNESDAY -> "Все акции в среду"
            THURSDAY -> "Все акции в четверг"
            FRIDAY -> "Все акции в пятницу"
            SATURDAY -> "Все акции в субботу"
            SUNDAY -> "Все акции в воскресенье"
            NOW -> "Все акции сейчас"
            TODAY -> "Все акции на сегодня"
            TOMORROW -> "Все акции на завтра"
            DAY_AFTER_TOMORROW -> "Все акции послезавтра"
            BIRTHDAY -> "Все акции на день рождения"
        }
    }

    fun getEnding(): String {
        return when(this) {
            MONDAY -> " в понедельник"
            TUESDAY -> " во вторник"
            WEDNESDAY -> " в среду"
            THURSDAY -> " в четверг"
            FRIDAY -> " в пятницу"
            SATURDAY -> " в субботу"
            SUNDAY -> " в воскресенье"
            NOW -> " сейчас"
            TODAY -> " сегодня"
            TOMORROW -> " завтра"
            DAY_AFTER_TOMORROW -> " послезавтра"
            BIRTHDAY -> " в день рождения"
        }
    }

}