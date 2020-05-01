package com.wellcome.main.service.management.admin

import com.wellcome.main.component.SynchronizeObjectProvider
import com.wellcome.main.dto.admin.response.SelectionOfferResponse
import com.wellcome.main.entity.selection.SelectionOffer
import com.wellcome.main.service.extentions.generators.admin.generateSelectionOfferDtoList
import com.wellcome.main.service.facade.institution.InstitutionOfferService
import com.wellcome.main.service.facade.selection.SelectionOfferService
import com.wellcome.main.service.facade.selection.SelectionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface AdminSelectionOfferManagementService {
    fun selectOffer(offerId: Long, selectionId: Long)
    fun getAll(selectionId: Long): SelectionOfferResponse
    fun remove(selectionOfferId: Long)
}

@Service
open class DefaultAdminSelectionOfferManagementService @Autowired constructor(
    private val selectionService: SelectionService,
    private val selectionOfferService: SelectionOfferService,
    private val institutionOfferService: InstitutionOfferService,
    private val synchronizedObjectProvider: SynchronizeObjectProvider
) : AdminSelectionOfferManagementService {

    @Transactional(readOnly = true)
    override fun getAll(selectionId: Long): SelectionOfferResponse {
        val selectionOffers =
            selectionOfferService.findBySelection(selectionId)
        return SelectionOfferResponse(selectionOffers.generateSelectionOfferDtoList())
    }

    @Transactional
    override fun selectOffer(offerId: Long, selectionId: Long) {
        val sync = synchronizedObjectProvider.getSelectionAndSelectionOfferSharedSynchronizedObject()
        synchronized(sync) {
            val offer = institutionOfferService.findById(offerId)

            val selection = selectionService.findById(selectionId)

            selectionOfferService.findByOffer(offer.id!!)
                ?: SelectionOffer(selection, offer).let(selectionOfferService::saveOrUpdate)
        }
    }

    @Transactional
    override fun remove(selectionOfferId: Long) {
        val selectionOffer = selectionOfferService.findById(selectionOfferId)
        selectionOfferService.deleteById(selectionOffer.id!!)
    }
}