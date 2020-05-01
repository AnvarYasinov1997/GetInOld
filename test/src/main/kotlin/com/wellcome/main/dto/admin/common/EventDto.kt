package com.wellcome.main.dto.admin.common

data class EventDto(val id: Long,
                    val title: String,
                    val pictureUrl: String,
                    val description: String,
                    val startWork: String,
                    val date: String,
                    val square: Boolean,
                    val price: PriceDto)