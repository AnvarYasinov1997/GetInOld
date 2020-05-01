package com.wellcome.main.dto.moderation.request.v1

import com.wellcome.main.dto.admin.common.EventDto
import com.wellcome.main.dto.admin.common.OfferDto

data class InstitutionEditRequestRequestV1(val institutionId: Long,
                                           val avatarUrl: String?,
                                           val description: String?,
                                           val deletePictureIds: List<Long>,
                                           val deleteOfferIds: List<Long>,
                                           val deleteEventIds: List<Long>,
                                           val newPictures: List<String>,
                                           val newOffers: List<OfferDto>,
                                           val newEvents: List<EventDto>)