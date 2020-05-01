package com.wellcome.main.dto.api.newDto.common.v1

data class EventDtoV1(val id: Long,
                      val title: String,
                      val pictureUrl: String,
                      val description: String,
                      val startWork: String,
                      val startDate: String,
                      val square: Boolean,
                      val price: String,
                      val institutionDto: InstitutionDtoV1,
                      val saved: Boolean)

data class BlockEventDtoV1(
    val title: String,
    val events: List<EventDtoV1>
)