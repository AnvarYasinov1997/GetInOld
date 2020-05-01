package com.wellcome.main.dto.api.newDto.response.v1

import com.wellcome.main.dto.api.newDto.common.v1.*

data class DynamicSearchResponseV1(val categoryOption: CategoryOptionDtoV1,
                                   val timeOptionDto: TimeOptionDtoV1,
                                   val recommendedInstitutions: BlockInstitutionsDtoV1,
                                   val recommendedOffers: BlockOffersDtoV1,
                                   val todayEvents: BlockEventDtoV1)


