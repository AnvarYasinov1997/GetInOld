package com.wellcome.main.dto.admin.common

data class OfferDto(val id: Long,
                    val title: String,
                    val offerType: String,
                    val description: String,
                    val isBirthday: Boolean,
                    val startDate: String,
                    val endDate: String,
                    val worksUp: List<WorksUpDto>)

