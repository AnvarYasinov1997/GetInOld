package com.wellcome.main.dto.web.common

data class EventDto(val id: Long,
                    val title: String,
                    val pictureUrl: String,
                    val description: String,
                    val startWork: String,
                    val startDate: String,
                    val square: Boolean,
                    val price: String,
                    val institutionDto: InstitutionDto,
                    val saved: Boolean)