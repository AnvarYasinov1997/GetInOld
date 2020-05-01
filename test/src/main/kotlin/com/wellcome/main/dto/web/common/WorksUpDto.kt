package com.wellcome.main.dto.web.common

data class WorksUpDto(val dayOfWeek: String,
                      val startWork: String,
                      val endWork: String,
                      val closed: Boolean,
                      val always: Boolean)