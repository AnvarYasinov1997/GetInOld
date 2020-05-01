package com.wellcome.main.dto.api.newDto.response.v2

import com.wellcome.main.dto.api.newDto.common.v1.*

data class ProfileResponseV2(val institutionDto: InstitutionDtoV1,
                             val worksUpList: List<WorksUpDtoV1>,
                             val partnerPictures: List<String>,
                             val currentUserReview: ReviewDtoV1?,
                             val tags: List<String>,
                             val reviews: BlockReviewDtoV1,
                             val offers: BlockOffersDtoV1,
                             val events: BlockEventDtoV1)