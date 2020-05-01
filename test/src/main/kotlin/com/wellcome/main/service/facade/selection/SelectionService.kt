package com.wellcome.main.service.facade.selection

import com.wellcome.main.entity.selection.Selection
import com.wellcome.main.repository.local.postgre.SelectionRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface SelectionService : BaseService<Selection> {
    fun findByName(name: String): Selection?
}

@Service
open class DefaultSelectionService @Autowired constructor(
    private val selectionRepository: SelectionRepository
) : SelectionService,
    DefaultBaseService<Selection>(Selection::class.java.simpleName, selectionRepository) {

    @Transactional
    override fun findByName(name: String): Selection? =
        selectionRepository.findByName(name).orElseGet {
            null
        }

}