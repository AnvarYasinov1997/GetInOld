package com.wellcome.main.dto.api.newDto.response.v2

import com.wellcome.main.dto.api.newDto.common.v1.EventDtoV1
import com.wellcome.main.dto.api.newDto.common.v1.InstitutionDtoV1
import com.wellcome.main.dto.api.newDto.common.v1.SelectionDtoV1
import com.wellcome.main.dto.api.newDto.common.v1.StoryDtoV1
import com.wellcome.main.dto.api.newDto.common.v2.OfferDtoV2

data class DynamicSearchResponseV2(val recommendedInstitutions: List<InstitutionDtoV1>,
                                   val recommendedOffers: List<OfferDtoV2>,
                                   val closestEvents: List<EventDtoV1>,
                                   val usefulStoryDtoList: List<StoryDtoV1>,
                                   val interestingStoryDtoList: List<StoryDtoV1>,
                                   val selectionDtoList: List<SelectionDtoV1>)