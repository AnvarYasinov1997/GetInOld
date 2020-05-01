package com.wellcome.main.service.management.admin

import com.wellcome.main.component.SynchronizeObjectProvider
import com.wellcome.main.dto.admin.common.SelectionDto
import com.wellcome.main.dto.admin.response.AllSelectionResponse
import com.wellcome.main.entity.selection.Selection
import com.wellcome.main.entity.selection.SelectionOffer
import com.wellcome.main.service.facade.selection.SelectionOfferService
import com.wellcome.main.service.facade.selection.SelectionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface AdminSelectionManagementService {
    fun add(selectionName: String)
    fun getAll(): AllSelectionResponse
    fun remove(selectionId: Long)
}

@Service
open class DefaultAdminSelectionManagementService @Autowired constructor(
    private val selectionService: SelectionService,
    private val selectionOfferService: SelectionOfferService,
    private val synchronizedObjectProvider: SynchronizeObjectProvider
) : AdminSelectionManagementService {

    @Transactional
    override fun add(selectionName: String) {
        selectionService.findByName(selectionName)
            ?: Selection(selectionName).let(selectionService::saveOrUpdate)
    }

    @Transactional(readOnly = true)
    override fun getAll(): AllSelectionResponse {
        return selectionService.findAll().map {
            SelectionDto(
                id = requireNotNull(it.id),
                name = it.name)
        }.let(::AllSelectionResponse)
    }

    @Transactional
    override fun remove(selectionId: Long) {
        val sync = synchronizedObjectProvider.getSelectionAndSelectionOfferSharedSynchronizedObject()
        synchronized(sync) {
            val selection = selectionService.findById(selectionId)
            selectionOfferService.findBySelection(selection.id!!)
                .mapNotNull(SelectionOffer::id)
                .forEach(selectionOfferService::deleteById)

            selectionService.deleteById(selection.id!!)
        }
    }
}