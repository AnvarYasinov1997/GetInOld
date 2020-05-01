package com.wellcome.main.dto.moderation.response.v1

import com.wellcome.main.dto.api.newDto.common.v1.EventDtoV1
import com.wellcome.main.dto.api.newDto.common.v1.InstitutionDtoV1
import com.wellcome.main.dto.api.newDto.common.v1.OfferDtoV1
import com.wellcome.main.dto.api.newDto.common.v1.PictureDtoV1

data class InstitutionModerationResponseForEditV1(val institutionDto: InstitutionDtoV1,
                                                  val pictures: List<PictureDtoV1>,
                                                  val offers: List<OfferDtoV1>,
                                                  val events: List<EventDtoV1>)