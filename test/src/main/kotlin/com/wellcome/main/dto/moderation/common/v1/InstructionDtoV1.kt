package com.wellcome.main.dto.moderation.common.v1

data class InstructionDtoV1(val id: Long,
                            val title: String,
                            val type: String,
                            val actionType: String,
                            val backgroundColor: String)