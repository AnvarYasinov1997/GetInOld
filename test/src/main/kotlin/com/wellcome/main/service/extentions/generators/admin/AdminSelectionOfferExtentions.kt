package com.wellcome.main.service.extentions.generators.admin

import com.wellcome.main.dto.admin.common.SelectionOfferDto
import com.wellcome.main.entity.selection.SelectionOffer

fun List<SelectionOffer>.generateSelectionOfferDtoList(): List<SelectionOfferDto> =
    this.map {
        SelectionOfferDto(
            id = requireNotNull(it.id),
            offerDto = it.offer.generateOfferDto()
        )
    }