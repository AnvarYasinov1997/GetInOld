package com.wellcome.main.service.extentions.generators.admin

import com.wellcome.main.dto.admin.common.PriceDto
import com.wellcome.main.entity.Price

fun Price.generatePriceDto(): PriceDto =
    PriceDto(
        lowerAmount = this.lowerAmount.toDouble(),
        topAmount = this.topAmount.toDouble(),
        fixAmount = this.fixAmount.toDouble(),
        fixPrice = this.fixPrice,
        free = this.free,
        currencyType = this.currencyType.name
    )
