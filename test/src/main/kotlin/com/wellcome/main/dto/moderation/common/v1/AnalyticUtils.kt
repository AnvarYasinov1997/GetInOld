package com.wellcome.main.dto.moderation.common.v1

data class AnalyticConversionDtoV1(
    val typeDto: AnalyticConversionTypeDto,
    val count: Long
)

data class AnalyticEventDtoV1(
    val type: AnalyticEventDtoType,
    val count: Long
)

enum class AnalyticConversionTypeDto {
    TAXI, CALL, MAP, SHARE, SAVE, REVIEW
}

enum class AnalyticEventDtoType {
    OFFER, OFFER_EXPAND, EVENT, EVENT_EXPAND, INSTITUTION_CARD, INSTITUTION_PROFILE
}