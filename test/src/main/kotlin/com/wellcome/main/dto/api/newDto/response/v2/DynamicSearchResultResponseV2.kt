package com.wellcome.main.dto.api.newDto.response.v2

import com.wellcome.main.dto.api.newDto.common.v1.BlockEventDtoV1
import com.wellcome.main.dto.api.newDto.common.v1.BlockInstitutionsDtoV1
import com.wellcome.main.dto.api.newDto.common.v1.BlockOffersDtoV1
import com.wellcome.main.dto.api.newDto.common.v1.StoryDtoV1

data class DynamicSearchResultResponseV2(val title: String,
                                         val recommendedInstitutions: BlockInstitutionsDtoV1,
                                         val offers: BlockOffersDtoV1,
                                         val closestInstitutions: BlockInstitutionsDtoV1,
                                         val allInstitutions: BlockInstitutionsDtoV1,
                                         val otherOffers: BlockOffersDtoV1,
                                         val categoryInstitutionEvents: BlockEventDtoV1)