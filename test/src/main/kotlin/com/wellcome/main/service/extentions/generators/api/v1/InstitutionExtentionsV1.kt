package com.wellcome.main.service.extentions.generators.api.v1

import com.wellcome.main.dto.api.newDto.common.v1.*
import com.wellcome.main.dto.api.newDto.response.v1.ProfileResponseV1
import com.wellcome.main.entity.institution.DayOfWeeks
import com.wellcome.main.entity.institution.Institution
import com.wellcome.main.entity.institution.InstitutionContactPhone
import com.wellcome.main.entity.institution.InstitutionReview
import com.wellcome.main.service.extentions.generators.api.common.generateCategoryDtoV1List
import com.wellcome.main.service.extentions.generators.api.common.generateInstitutionDtoV1
import com.wellcome.main.service.extentions.generators.api.common.generateOfferDtoV1
import com.wellcome.main.service.extentions.generators.api.common.generateReviewDtoV1
import com.wellcome.main.wrapper.Delegate
import com.wellcome.main.wrapper.EntityWrapper

fun List<EntityWrapper<Institution>>.generateInstitutionMapDtoV1List(dayOfWeek: DayOfWeeks): List<InstitutionMapDtoV1> =
    this.map {
        var timeDelegate: Delegate.TimeDelegate? = null

        it.delegates.forEach { delegate ->
            when (delegate) {
                is Delegate.TimeDelegate -> timeDelegate = delegate
            }
        }
        val worksUp = it.entity.workTime.first { workTime -> workTime.dayOfWeek == dayOfWeek }
        InstitutionMapDtoV1(
            id = it.entity.getIdNotNull(),
            lat = it.entity.locationAttributes.lat,
            lon = it.entity.locationAttributes.lon,
            categoryList = it.entity.categories.toList().generateCategoryDtoV1List(),
            address = it.entity.locationAttributes.address,
            phoneList = it.entity.contactPhones.map(InstitutionContactPhone::phoneNumber),
            worksUpDto = WorksUpDtoV1(
                startWork = worksUp.startDay,
                endWork = worksUp.endDay,
                dayOfWeek = worksUp.dayOfWeek.name,
                closed = timeDelegate?.open?.not() ?: worksUp.closed,
                always = worksUp.startDay == "00:00" && worksUp.endDay == "00:00"
            )
        )
    }

fun EntityWrapper<Institution>.generateProfileResponseV1(dayOfWeek: DayOfWeeks, partnerPictures: List<String>, userId: Long? = null, reviewsCount: Long, offersCount: Long): ProfileResponseV1 {
    var userDelegate: Delegate.UserDelegate? = null

    this.delegates.forEach { delegate ->
        when (delegate) {
            is Delegate.UserDelegate -> userDelegate = delegate
        }
    }
    return ProfileResponseV1(
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
        partnerPictures = partnerPictures,
        reviews = BlockReviewDtoV1(
            title = "Отзывы",
            reviews = this.entity.reviews.map(InstitutionReview::generateReviewDtoV1).take(reviewsCount.toInt()),
            showAll = this.entity.reviews.size > reviewsCount
        ),
        offers = BlockOffersDtoV1(
            title = "Акции",
            offers = this.entity.getWorkingOffers().take(offersCount.toInt()).map {
                it.generateOfferDtoV1(dayOfWeek, userDelegate?.saved ?: false, userDelegate?.rated ?: false)
            },
            showAll = this.entity.getWorkingOffers().size > offersCount,
            blockType = BlockType.INSTITUTION_PROFILE_OFFERS.name
        ),
        events = BlockEventDtoV1(
            title = "Афиша",
            events = emptyList()
        ),
        currentUserReview = userId?.let { this.entity.reviews.firstOrNull { review -> review.user.id == it }?.generateReviewDtoV1() }
    )
}