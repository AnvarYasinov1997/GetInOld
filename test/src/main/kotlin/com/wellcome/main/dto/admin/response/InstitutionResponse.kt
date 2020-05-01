package com.wellcome.main.dto.admin.response

import com.wellcome.main.dto.admin.common.*

data class InstitutionModerationResponse(val institutionId: Long,
                                         val remainingBarsCount: Int,
                                         val name: String,
                                         val avatarUrl: String,
                                         val description: String,
                                         val comments: String,
                                         val address: String,
                                         val rating: Double,
                                         val numberOfPeopleRated: Long,
                                         val instagram: String,
                                         val tags: List<TagDto>,
                                         val tagPatterns: List<String>,
                                         val categories: List<CategoryDto>,
                                         val pictures: List<PictureDto>,
                                         val offers: List<OfferDto>,
                                         val events: List<EventDto>,
                                         val phones: List<PhoneDto>,
                                         val blocked: Boolean,
                                         val worksUp: List<WorksUpDto>,
                                         val selectionDtoList: List<SelectionDto>,
                                         val editRequestDto: InstitutionEditRequestDto?)

data class InstitutionNameResponse(val nameDto: List<InstitutionNameDto>)

data class InstitutionNameDto(val id: Long,
                              val name: String,
                              val lastModerationDate: String?,
                              val priority: ModerationPriority?,
                              val blocked: Boolean)

enum class ModerationPriority {
    LOW, MIDDLE, HIGH
}

data class PictureDto(val id: Long,
                      val url: String)

data class PhoneDto(val id: Long,
                    val phoneNumber: String)
