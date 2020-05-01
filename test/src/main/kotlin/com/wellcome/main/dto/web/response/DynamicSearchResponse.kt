package com.wellcome.main.dto.web.response

import com.wellcome.main.dto.web.common.*

data class DynamicSearchResponse(val recommendedInstitutions: List<InstitutionDto>,
                                 val recommendedOffers: List<OfferDto>,
                                 val closestEvents: List<EventDto>,
                                 val usefulStoryDtoList: List<StoryDto>,
                                 val interestingStoryDtoList: List<StoryDto>,
                                 val selectionDtoList: List<SelectionDto>,
                                 val birthdayCampaignsDto: BirthdayCampaignsDto?)