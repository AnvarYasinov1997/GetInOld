package com.wellcome.main.dto.web.common

data class OfferDto(val id: Long,
                    val title: String,
                    val description: String,
                    val type: String,
                    val latestCheck: String,
                    val photoUrl: String,
                    val birthday: Boolean,
                    val institutionDto: InstitutionDto,
                    val worksUpDtoList: List<WorksUpDto>)

data class BlockOffersDto(val title: String,
                            val blockType: String,
                            val offers: List<OfferDto>,
                            val showAll: Boolean)