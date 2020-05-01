package com.wellcome.main.dto.api.newDto.response.v1

import com.wellcome.main.dto.api.newDto.common.v1.BlockEventDtoV1
import com.wellcome.main.dto.api.newDto.common.v1.BlockInstitutionsDtoV1
import com.wellcome.main.dto.api.newDto.common.v1.BlockOffersDtoV1

data class DynamicSearchResultResponseV1(val title: String,
                                         val recommendedInstitutions: BlockInstitutionsDtoV1,
                                         val offers: BlockOffersDtoV1,
                                         val allInstitutions: BlockInstitutionsDtoV1,
                                         val otherOffers: BlockOffersDtoV1,
                                         val categoryInstitutionEvents: BlockEventDtoV1)