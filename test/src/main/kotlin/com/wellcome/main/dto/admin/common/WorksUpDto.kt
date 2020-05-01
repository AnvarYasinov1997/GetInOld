package com.wellcome.main.dto.admin.common

data class WorksUpDto(val dayOfWeek: String,
                      val startWork: String,
                      val endWork: String,
                      val closed: Boolean,
                      val always: Boolean)