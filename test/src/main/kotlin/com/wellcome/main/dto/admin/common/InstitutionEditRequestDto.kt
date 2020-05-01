package com.wellcome.main.dto.admin.common

import com.wellcome.main.dto.admin.response.PictureDto

data class InstitutionEditRequestDto(val institutionEditRequestId: Long,
                                     val status: String,
                                     val developerMessage: String,
                                     val avatarUrl: String,
                                     val description: String,
                                     val newPictures: List<PictureDto>,
                                     val deletePictures: List<PictureDto>,
                                     val newOffers: List<OfferDto>,
                                     val newEvents: List<EventDto>,
                                     val deleteOffers: List<OfferDto>,
                                     val deleteEvents: List<EventDto>)

data class InstitutionEditRequestFeedbackDto(val institutionEditRequestId: Long,
                                             val message: String,
                                             val approve: Boolean)