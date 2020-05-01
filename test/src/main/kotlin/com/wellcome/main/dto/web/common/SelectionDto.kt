package com.wellcome.main.dto.web.common

data class SelectionDto(val id: Long,
                        val name: String,
                        val offerDtoList: List<OfferDto>)