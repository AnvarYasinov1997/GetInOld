package com.wellcome.main.controller.admin

import com.wellcome.main.dto.admin.request.InstructionRequest
import com.wellcome.main.dto.admin.response.InstructionResponse
import com.wellcome.main.service.management.admin.AdminInstructionManagementService
import com.wellcome.main.util.variables.Paths
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value =[Paths.BASE_INSTRUCTION])
open class AdminInstructionController @Autowired constructor(
    private val adminInstructionManagementService: AdminInstructionManagementService
) {

    @PostMapping(value = [Paths.Instruction.ADD])
    open fun add(@RequestBody request: InstructionRequest) {
        adminInstructionManagementService.add(request)
    }

    @GetMapping(value = [Paths.Instruction.GET_ALL])
    open fun getAll(): InstructionResponse {
        return  adminInstructionManagementService.getAll()
    }

}