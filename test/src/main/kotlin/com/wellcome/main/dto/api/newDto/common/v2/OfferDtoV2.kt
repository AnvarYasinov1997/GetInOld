package com.wellcome.main.dto.api.newDto.common.v2

import com.wellcome.main.dto.api.newDto.common.v1.InstitutionDtoV1
import com.wellcome.main.dto.api.newDto.common.v1.WorksUpDtoV1

data class OfferDtoV2(val id: Long,
                      val title: String,
                      val description: String,
                      val type: String,
                      val latestCheck: String,
                      val photoUrl: String,
                      val birthday: Boolean,
                      val institutionDto: InstitutionDtoV1,
                      val worksUpDtoList: List<WorksUpDtoV1>)

data class BlockOffersDtoV2(val title: String,
                            val blockType: String,
                            val offers: List<OfferDtoV2>,
                            val showAll: Boolean)