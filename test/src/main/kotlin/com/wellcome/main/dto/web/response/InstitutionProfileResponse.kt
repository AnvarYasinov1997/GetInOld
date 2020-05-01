package com.wellcome.main.dto.web.response

import com.wellcome.main.dto.web.common.*

data class InstitutionProfileResponse(val institutionDto: InstitutionDto,
                                      val worksUpList: List<WorksUpDto>,
                                      val currentUserReview: ReviewDto?,
                                      val tags: List<String>,
                                      val reviews: List<ReviewDto>,
                                      val offers: List<OfferDto>,
                                      val events: List<EventDto>)