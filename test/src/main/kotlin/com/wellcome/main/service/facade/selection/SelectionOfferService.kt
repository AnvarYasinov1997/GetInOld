package com.wellcome.main.service.facade.selection

import com.wellcome.main.entity.selection.SelectionOffer
import com.wellcome.main.repository.local.postgre.SelectionOfferRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface SelectionOfferService : BaseService<SelectionOffer> {
    fun findByOffer(offerId: Long): SelectionOffer?
    fun findBySelection(selectionId: Long): List<SelectionOffer>
}

@Service
open class DefaultSelectionOfferService @Autowired constructor(
    private val selectionOfferRepository: SelectionOfferRepository
) : SelectionOfferService,
    DefaultBaseService<SelectionOffer>(SelectionOffer::class.java.simpleName, selectionOfferRepository) {

    @Transactional
    override fun findByOffer(offerId: Long): SelectionOffer? =
        selectionOfferRepository.findByOfferId(offerId).orElseGet { null }

    @Transactional(readOnly = true)
    override fun findBySelection(selectionId: Long): List<SelectionOffer> =
        selectionOfferRepository.findBySelectionId(selectionId)

}