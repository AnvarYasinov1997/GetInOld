package com.wellcome.main.dto.admin.request

data class InstructionRequest(val title: String,
                              val text :String,
                              val backgroundColor: String,
                              val type: String,
                              val actionType: String)