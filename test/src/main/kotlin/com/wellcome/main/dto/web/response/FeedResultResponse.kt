package com.wellcome.main.dto.web.response

import com.wellcome.main.dto.web.common.EventDto
import com.wellcome.main.dto.web.common.InstitutionDto
import com.wellcome.main.dto.web.common.OfferDto

data class FeedResultStepOneResponse(val offers: List<OfferDto>,
                                     val categoryInstitutionEvents: List<EventDto>,
                                     val recommendedInstitutions: List<InstitutionDto>)

data class FeedResultStepTwoResponse(val allInstitutions: List<InstitutionDto>)

data class FeedResultStepThreeResponse(val otherOffers: List<OfferDto>)