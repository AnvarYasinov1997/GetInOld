package com.wellcome.main.service.extentions.generators.admin

import com.wellcome.main.dto.admin.common.CategoryDto
import com.wellcome.main.dto.admin.common.SelectionDto
import com.wellcome.main.dto.admin.common.TagDto
import com.wellcome.main.dto.admin.response.InstitutionModerationResponse
import com.wellcome.main.dto.admin.response.InstitutionNameDto
import com.wellcome.main.dto.admin.response.ModerationPriority
import com.wellcome.main.dto.admin.response.PictureDto
import com.wellcome.main.entity.institution.*
import com.wellcome.main.entity.institutionProfile.InstitutionEditRequest
import com.wellcome.main.entity.selection.Selection
import java.time.ZonedDateTime

fun Institution.generateInstitutionModerationResponse(remainingBarsCount: Int,
                                                      editRequest: InstitutionEditRequest?,
                                                      institutionCategories: List<InstitutionCategory>,
                                                      selections: List<Selection>): InstitutionModerationResponse {
    return InstitutionModerationResponse(
        institutionId = requireNotNull(this.id),
        remainingBarsCount = remainingBarsCount,
        name = this.name,
        rating = this.rating,
        numberOfPeopleRated = this.peopleOfRatedCount,
        description = this.description,
        comments = this.comments ?: "",
        address = this.locationAttributes.address,
        avatarUrl = this.avatarUrl,
        instagram = this.instagramAccount ?: "",
        tags = this.tags.map {
            TagDto(requireNotNull(it.id), it.name)
        },
        categories = institutionCategories.map {
            val checked = this.categories.let { categories ->
                for (category in categories) {
                    if (category.categoryType == it.categoryType) return@let true
                }
                return@let false
            }
            CategoryDto(requireNotNull(it.id), it.categoryType.toString(), checked)
        },
        worksUp = this.workTime.generateWorksUpDtoList(),
        pictures = this.pictures.map {
            PictureDto(requireNotNull(it.id), it.pictureUrl)
        },
        selectionDtoList = selections.map { selection ->
            SelectionDto(requireNotNull(selection.id), selection.name)
        },
        offers = this.getWorkingOffers().filter(InstitutionOffer::active).generateOfferDtoList(),
        events = this.events.filterNot(InstitutionEvent::completed).generateEventDtoList(),
        phones = this.contactPhones.generatePhoneDtoList(),
        blocked = this.blocked,
        tagPatterns = InstitutionTagType.values().map(InstitutionTagType::name),
        editRequestDto = editRequest?.generateInstitutionEditRequestDto()
    )
}

fun Institution.generateInstitutionNameDto(priority: ModerationPriority? = null): InstitutionNameDto {
    val lastModerationDateTime = this.updateEntityDateTime ?: this.createEntityDateTime
    return InstitutionNameDto(
        id = requireNotNull(this.id),
        name = this.name,
        lastModerationDate = lastModerationDateTime?.let(ZonedDateTime::parse)?.toLocalDate()?.toString(),
        priority = priority,
        blocked = this.blocked)
}