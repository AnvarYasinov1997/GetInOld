package com.wellcome.main.service.extentions.generators.api.common

import com.wellcome.main.dto.api.newDto.common.v1.SelectionDtoV1
import com.wellcome.main.entity.institution.DayOfWeeks
import com.wellcome.main.entity.selection.Selection
import com.wellcome.main.entity.selection.SelectionOffer
import com.wellcome.main.service.extentions.generators.api.common.v2.generateOfferDtoV2List
import com.wellcome.main.wrapper.EntityWrapper

fun List<Selection>.generateSelectionDtoV1List(dayOfWeek: DayOfWeeks, selectionOffers: List<EntityWrapper<SelectionOffer>>): List<SelectionDtoV1> =
    this.map {
        val offersBySelection =
            selectionOffers.filter { wrapper -> wrapper.entity.selection == it }
                .map { wrapper -> EntityWrapper(wrapper.entity.offer, wrapper.delegates) }
        SelectionDtoV1(
            id = it.getIdNotNull(),
            name = it.name,
            offerDtoList = offersBySelection.generateOfferDtoV2List(dayOfWeek)
        )
    }