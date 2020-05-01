package com.wellcome.main.service.management.admin

import com.wellcome.main.dto.admin.common.InstructionDto
import com.wellcome.main.dto.admin.request.InstructionRequest
import com.wellcome.main.dto.admin.response.InstructionResponse
import com.wellcome.main.entity.BackgroundColor
import com.wellcome.main.entity.Instruction
import com.wellcome.main.entity.InstructionActionType
import com.wellcome.main.entity.InstructionType
import com.wellcome.main.service.facade.InstructionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface AdminInstructionManagementService {
    fun add(request: InstructionRequest)
    fun getAll(): InstructionResponse
}

@Service
open class DefaultAdminInstructionManagementService @Autowired constructor(
    private val instructionService: InstructionService
) : AdminInstructionManagementService {

    @Transactional
    override fun add(request: InstructionRequest) {
        Instruction(
            title = request.title,
            text = request.text,
            type = InstructionType.valueOf(request.type),
            actionType = InstructionActionType.valueOf(request.actionType),
            backgroundColor = BackgroundColor.valueOf(request.backgroundColor)
        ).let(instructionService::saveOrUpdate)
    }

    @Transactional(readOnly = true)
    override fun getAll(): InstructionResponse {
        return instructionService.findAll().map {
            InstructionDto(
                id = it.id!!,
                text = it.text,
                title = it.title,
                type = it.type.name,
                actionType = it.actionType.name,
                backgroundColor = it.backgroundColor.name
            )
        }.let(::InstructionResponse)
    }
}