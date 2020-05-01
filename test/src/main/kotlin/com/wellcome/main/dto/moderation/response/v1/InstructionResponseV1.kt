package com.wellcome.main.dto.moderation.response.v1

data class InstructionResponseV1(val id: Long,
                                 val title: String,
                                 val type: String,
                                 val actionType: String,
                                 val backgroundColor: String,
                                 val text: String)