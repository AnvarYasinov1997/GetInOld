package com.wellcome.main.service.extentions.generators.moderation.common

import com.wellcome.main.dto.moderation.common.v1.MarketingDtoV1
import com.wellcome.main.entity.Marketing

fun List<Marketing>.generateMarketingDtoV1List(): List<MarketingDtoV1> =
    this.map {
        MarketingDtoV1(
            id = it.id!!,
            title = it.title,
            pictureUrl = it.pictureUrl
        )
    }