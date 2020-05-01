package com.wellcome.main.service.facade

import com.wellcome.main.entity.Instruction
import com.wellcome.main.repository.local.postgre.InstructionRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

interface InstructionService : BaseService<Instruction>

@Service
open class DefaultInstructionService @Autowired constructor(
    private val instructionRepository: InstructionRepository
) : DefaultBaseService<Instruction>(Instruction::class.java.simpleName, instructionRepository),
    InstructionService