package com.wellcome.main.dto.api.newDto.common.v1

data class WorksUpDtoV1(val dayOfWeek: String,
                        val startWork: String,
                        val endWork: String,
                        val closed: Boolean,
                        val always: Boolean)