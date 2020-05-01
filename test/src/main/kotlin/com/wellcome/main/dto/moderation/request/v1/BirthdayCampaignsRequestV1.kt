package com.wellcome.main.dto.moderation.request.v1

data class BirthdayCampaignsRequestV1(val institutionId: Long,
                                      val text: String,
                                      val age: String,
                                      val gender: String,
                                      val expirationTime: String)

enum class BirthdayCampaignAge {
    LOW_AGE, MIDDLE_AGE, HIGH_AGE, ALL_AGE
}

enum class BirthdayCampaignTime {
    ONE_MONTH
}