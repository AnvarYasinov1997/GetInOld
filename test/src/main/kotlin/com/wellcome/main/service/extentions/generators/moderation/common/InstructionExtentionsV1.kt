package com.wellcome.main.service.extentions.generators.moderation.common

import com.wellcome.main.dto.moderation.common.v1.InstructionDtoV1
import com.wellcome.main.entity.Instruction

fun List<Instruction>.generateInstructionDtoV1List(): List<InstructionDtoV1> =
    this.map {
        InstructionDtoV1(
            id = it.id!!,
            title = it.title,
            type = it.type.name,
            actionType = it.actionType.name,
            backgroundColor = it.backgroundColor.hexCode
        )
    }