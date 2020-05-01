package com.wellcome.main.dto.moderation.common.v1

import com.wellcome.main.dto.api.newDto.common.v1.EventDtoV1

data class EventAnalyticDtoV1(val eventDto: EventDtoV1,
                              val createDate: String?,
                              val expireDate: String?)