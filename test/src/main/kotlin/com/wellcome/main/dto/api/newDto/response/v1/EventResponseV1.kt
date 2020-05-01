package com.wellcome.main.dto.api.newDto.response.v1

import com.wellcome.main.dto.api.newDto.common.v1.BlockEventDtoV1
import com.wellcome.main.dto.api.newDto.common.v1.EventDtoV1

data class EventResponseV1(
    val promotedEvents: List<EventDtoV1>,
    val currentEvents: List<BlockEventDtoV1>,
    val futureEvents: List<EventDtoV1>
)

