package com.wellcome.main.service.extentions.generators.api.v3

import com.wellcome.main.dto.api.newDto.common.v1.WorksUpDtoV1
import com.wellcome.main.dto.api.newDto.response.v3.ProfileResponseV3
import com.wellcome.main.entity.institution.DayOfWeeks
import com.wellcome.main.entity.institution.Institution
import com.wellcome.main.entity.institution.InstitutionEvent
import com.wellcome.main.entity.institution.InstitutionTag
import com.wellcome.main.service.extentions.generators.api.common.generateEventDtoV1
import com.wellcome.main.service.extentions.generators.api.common.generateInstitutionDtoV1
import com.wellcome.main.service.extentions.generators.api.common.generateReviewDtoV1
import com.wellcome.main.service.extentions.generators.api.common.v2.generateOfferDtoV2
import com.wellcome.main.service.extentions.generators.api.common.v2.generateReviewDtoV2List
import com.wellcome.main.wrapper.Delegate
import com.wellcome.main.wrapper.EntityWrapper

fun EntityWrapper<Institution>.generateProfileResponseV3(dayOfWeek: DayOfWeeks,
                                                            userId: Long? = null): ProfileResponseV3 {
    var userDelegate: Delegate.UserDelegate? = null

    this.delegates.forEach { delegate ->
        when (delegate) {
            is Delegate.UserDelegate -> userDelegate = delegate
        }
    }
    return ProfileResponseV3(
        institutionDto = this.generateInstitutionDtoV1(dayOfWeek),
        worksUpList = this.entity.workTime.map { x ->
            WorksUpDtoV1(
                dayOfWeek = x.dayOfWeek.name,
                startWork = x.startDay,
                endWork = x.endDay,
                closed = x.closed,
                always = x.startDay == "00:00" && x.endDay == "00:00"
            )
        },
        reviews = this.entity.reviews.generateReviewDtoV2List(),
        tags = this.entity.tags.map(InstitutionTag::name),
        offers = this.entity.getWorkingOffers().map {
            it.generateOfferDtoV2(dayOfWeek, userDelegate?.saved ?: false, userDelegate?.rated ?: false)
        },
        events = this.entity.getWorkingEvents().map(InstitutionEvent::generateEventDtoV1),
        currentUserReview = userId?.let { this.entity.reviews.firstOrNull { review -> review.user.id == it }?.generateReviewDtoV1() }
    )
}