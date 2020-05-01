package com.wellcome.main.service.extentions.generators.web

import com.wellcome.main.dto.web.common.WorksUpDto
import com.wellcome.main.dto.web.response.InstitutionProfileResponse
import com.wellcome.main.entity.institution.DayOfWeeks
import com.wellcome.main.entity.institution.Institution
import com.wellcome.main.entity.institution.InstitutionEvent
import com.wellcome.main.entity.institution.InstitutionTag
import com.wellcome.main.service.extentions.generators.web.common.*
import com.wellcome.main.wrapper.Delegate
import com.wellcome.main.wrapper.EntityWrapper

fun EntityWrapper<Institution>.generateInstitutionProfileResponse(dayOfWeek: DayOfWeeks,
                                                                  userId: Long? = null): InstitutionProfileResponse {
    var userDelegate: Delegate.UserDelegate? = null

    this.delegates.forEach { delegate ->
        when (delegate) {
            is Delegate.UserDelegate -> userDelegate = delegate
        }
    }
    return InstitutionProfileResponse(
        institutionDto = this.generateInstitutionDto(dayOfWeek),
        worksUpList = this.entity.workTime.map { x ->
            WorksUpDto(
                dayOfWeek = x.dayOfWeek.name,
                startWork = x.startDay,
                endWork = x.endDay,
                closed = x.closed,
                always = x.startDay == "00:00" && x.endDay == "00:00"
            )
        },
        reviews = this.entity.reviews.generateReviewDtoList(),
        tags = this.entity.tags.map(InstitutionTag::name),
        offers = this.entity.getWorkingOffers().map {
            it.generateOfferDto(dayOfWeek, userDelegate?.saved ?: false, userDelegate?.rated ?: false)
        },
        events = this.entity.getWorkingEvents().map(InstitutionEvent::generateEventDto),
        currentUserReview = userId?.let { this.entity.reviews.firstOrNull { review -> review.user.id == it }?.generateReviewDto() }
    )
}