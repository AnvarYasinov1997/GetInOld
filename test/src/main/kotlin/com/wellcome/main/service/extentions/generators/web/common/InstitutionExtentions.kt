package com.wellcome.main.service.extentions.generators.web.common

import com.wellcome.main.dto.web.common.InstitutionDto
import com.wellcome.main.dto.web.common.WorksUpDto
import com.wellcome.main.entity.institution.DayOfWeeks
import com.wellcome.main.entity.institution.Institution
import com.wellcome.main.entity.institution.InstitutionContactPhone
import com.wellcome.main.entity.institution.InstitutionPicture
import com.wellcome.main.wrapper.Delegate
import com.wellcome.main.wrapper.EntityWrapper

fun Institution.generateInstitutionDto(): InstitutionDto {
    val worksUp = this.workTime.first { workTime -> workTime.dayOfWeek == DayOfWeeks.MONDAY }
    return InstitutionDto(
        id = this.id!!,
        name = this.name,
        description = this.description,
        rating = this.rating.toString().take(3).toDouble(),
        numberOfPeopleRated = this.peopleOfRatedCount,
        avatarUrl = this.avatarUrl,
        address = this.locationAttributes.address,
        pictureUrls = this.pictures.map { x -> x.pictureUrl },
        phones = this.contactPhones.map { x -> x.phoneNumber },
        categories = this.categories.toList().generateCategoryDtoList(),
        worksUp = WorksUpDto(
            startWork = worksUp.startDay,
            endWork = worksUp.endDay,
            dayOfWeek = worksUp.dayOfWeek.name,
            closed = worksUp.closed,
            always = worksUp.startDay == "00:00" && worksUp.endDay == "00:00"
        ),
        lat = this.locationAttributes.lat,
        lon = this.locationAttributes.lon,
        distance = null,
        saved = false,
        rated = false
    )
}

fun Institution.generateInstitutionDto(dayOfWeek: DayOfWeeks, distance: Int?, saved: Boolean, rated: Boolean): InstitutionDto {
    val worksUp = this.workTime.first { workTime -> workTime.dayOfWeek == dayOfWeek }
    return InstitutionDto(
        id = this.id!!,
        name = this.name,
        description = this.description,
        rating = this.rating.toString().take(3).toDouble(),
        numberOfPeopleRated = this.peopleOfRatedCount,
        avatarUrl = this.avatarUrl,
        address = this.locationAttributes.address,
        pictureUrls = this.pictures.map { x -> x.pictureUrl },
        phones = this.contactPhones.map { x -> x.phoneNumber },
        categories = this.categories.toList().generateCategoryDtoList(),
        worksUp = WorksUpDto(
            startWork = worksUp.startDay,
            endWork = worksUp.endDay,
            dayOfWeek = worksUp.dayOfWeek.name,
            closed = worksUp.closed,
            always = worksUp.startDay == "00:00" && worksUp.endDay == "00:00"
        ),
        lat = this.locationAttributes.lat,
        lon = this.locationAttributes.lon,
        distance = distance,
        saved = saved,
        rated = rated
    )
}

fun List<EntityWrapper<Institution>>.generateInstitutionDtoList(dayOfWeek: DayOfWeeks): List<InstitutionDto> {
    return this.map {
        var userDelegate: Delegate.UserDelegate? = null
        var timeDelegate: Delegate.TimeDelegate? = null

        it.delegates.forEach { delegate ->
            when (delegate) {
                is Delegate.UserDelegate -> userDelegate = delegate
                is Delegate.TimeDelegate -> timeDelegate = delegate
            }
        }
        val worksUp = it.entity.workTime.first { workTime -> workTime.dayOfWeek == dayOfWeek }
        return@map InstitutionDto(
            id = requireNotNull(it.entity.id),
            name = it.entity.name,
            description = it.entity.description,
            rating = it.entity.rating.toString().take(3).toDouble(),
            numberOfPeopleRated = it.entity.peopleOfRatedCount,
            avatarUrl = it.entity.avatarUrl,
            address = it.entity.locationAttributes.address,
            pictureUrls = it.entity.pictures.map(InstitutionPicture::pictureUrl),
            phones = it.entity.contactPhones.map(InstitutionContactPhone::phoneNumber),
            categories = it.entity.categories.toList().generateCategoryDtoList(),
            lat = it.entity.locationAttributes.lat,
            lon = it.entity.locationAttributes.lon,
            saved = userDelegate?.saved ?: false,
            rated = userDelegate?.rated ?: false,
            distance = null,
            worksUp = WorksUpDto(
                startWork = worksUp.startDay,
                endWork = worksUp.endDay,
                dayOfWeek = worksUp.dayOfWeek.name,
                closed = timeDelegate?.open?.not() ?: worksUp.closed,
                always = worksUp.startDay == "00:00" && worksUp.endDay == "00:00"
            )
        )
    }
}

fun EntityWrapper<Institution>.generateInstitutionDto(dayOfWeek: DayOfWeeks): InstitutionDto {
    var userDelegate: Delegate.UserDelegate? = null
    var timeDelegate: Delegate.TimeDelegate? = null

    this.delegates.forEach { delegate ->
        when (delegate) {
            is Delegate.UserDelegate -> userDelegate = delegate
            is Delegate.TimeDelegate -> timeDelegate = delegate
        }
    }
    val worksUp = this.entity.workTime.first { workTime -> workTime.dayOfWeek == dayOfWeek }
    return InstitutionDto(
        id = this.entity.id!!,
        name = this.entity.name,
        description = this.entity.description,
        rating = this.entity.rating.toString().take(3).toDouble(),
        numberOfPeopleRated = this.entity.peopleOfRatedCount,
        avatarUrl = this.entity.avatarUrl,
        address = this.entity.locationAttributes.address,
        pictureUrls = this.entity.pictures.map { x -> x.pictureUrl },
        phones = this.entity.contactPhones.map { x -> x.phoneNumber },
        worksUp = WorksUpDto(
            startWork = worksUp.startDay,
            endWork = worksUp.endDay,
            dayOfWeek = worksUp.dayOfWeek.name,
            closed = timeDelegate?.open?.not() ?: worksUp.closed,
            always = worksUp.startDay == "00:00" && worksUp.endDay == "00:00"
        ),
        categories = this.entity.categories.toList().generateCategoryDtoList(),
        lat = this.entity.locationAttributes.lat,
        lon = this.entity.locationAttributes.lon,
        distance = null,
        saved = userDelegate?.saved ?: false,
        rated = userDelegate?.rated ?: false
    )
}

