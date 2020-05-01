package com.wellcome.main.dto.moderation.response.v1

import com.wellcome.main.dto.api.newDto.common.v1.EventDtoV1
import com.wellcome.main.dto.api.newDto.common.v1.OfferDtoV1

data class ArchiveResponseV1(val offers: List<OfferDtoV1>,
                             val events: List<EventDtoV1>)