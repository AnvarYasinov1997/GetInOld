package com.wellcome.main.dto.admin.common

data class InstructionDto(val id: Long,
                          val text: String,
                          val title: String,
                          val type: String,
                          val actionType: String,
                          val backgroundColor: String)