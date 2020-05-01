package com.wellcome.main.dto.api.newDto.response.v3

import com.wellcome.main.dto.api.newDto.common.v1.EventDtoV1
import com.wellcome.main.dto.api.newDto.common.v1.InstitutionDtoV1
import com.wellcome.main.dto.api.newDto.common.v1.ReviewDtoV1
import com.wellcome.main.dto.api.newDto.common.v1.WorksUpDtoV1
import com.wellcome.main.dto.api.newDto.common.v2.OfferDtoV2
import com.wellcome.main.dto.api.newDto.common.v2.ReviewDtoV2

data class ProfileResponseV3(val institutionDto: InstitutionDtoV1,
                             val worksUpList: List<WorksUpDtoV1>,
                             val currentUserReview: ReviewDtoV1?,
                             val tags: List<String>,
                             val reviews: List<ReviewDtoV2>,
                             val offers: List<OfferDtoV2>,
                             val events: List<EventDtoV1>)