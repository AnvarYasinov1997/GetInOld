package com.wellcome.main.service.extentions.generators.web.common

import com.wellcome.main.dto.web.common.SelectionDto
import com.wellcome.main.entity.institution.DayOfWeeks
import com.wellcome.main.entity.selection.Selection
import com.wellcome.main.entity.selection.SelectionOffer
import com.wellcome.main.wrapper.EntityWrapper


fun List<Selection>.generateSelectionDtoList(dayOfWeek: DayOfWeeks, selectionOffers: List<EntityWrapper<SelectionOffer>>): List<SelectionDto> =
    this.map {
        val offersBySelection =
            selectionOffers.filter { wrapper -> wrapper.entity.selection == it }
                .map { wrapper -> EntityWrapper(wrapper.entity.offer, wrapper.delegates) }
        SelectionDto(
            id = it.getIdNotNull(),
            name = it.name,
            offerDtoList = offersBySelection.generateOfferDtoList(dayOfWeek)
        )
    }