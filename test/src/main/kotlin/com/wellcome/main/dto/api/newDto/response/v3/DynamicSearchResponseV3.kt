package com.wellcome.main.dto.api.newDto.response.v3

import com.wellcome.main.dto.api.newDto.common.v1.*
import com.wellcome.main.dto.api.newDto.common.v2.OfferDtoV2

data class DynamicSearchResponseV3(val recommendedInstitutions: List<InstitutionDtoV1>,
                                   val recommendedOffers: List<OfferDtoV2>,
                                   val closestEvents: List<EventDtoV1>,
                                   val usefulStoryDtoList: List<StoryDtoV1>,
                                   val interestingStoryDtoList: List<StoryDtoV1>,
                                   val selectionDtoList: List<SelectionDtoV1>,
                                   val birthdayCampaignsDto: BirthdayCampaignsDtoV1?)