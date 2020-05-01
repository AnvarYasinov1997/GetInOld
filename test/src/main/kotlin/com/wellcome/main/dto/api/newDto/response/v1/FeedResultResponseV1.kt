package com.wellcome.main.dto.api.newDto.response.v1

import com.wellcome.main.dto.api.newDto.common.v1.EventDtoV1
import com.wellcome.main.dto.api.newDto.common.v1.InstitutionDtoV1
import com.wellcome.main.dto.api.newDto.common.v2.OfferDtoV2

data class FeedResultStepOneResponseV1(val offers: List<OfferDtoV2>,
                                       val categoryInstitutionEvents: List<EventDtoV1>,
                                       val recommendedInstitutions: List<InstitutionDtoV1>)

data class FeedResultStepTwoResponseV1(val allInstitutions: List<InstitutionDtoV1>)

data class FeedResultStepThreeResponseV1(val otherOffers: List<OfferDtoV2>)