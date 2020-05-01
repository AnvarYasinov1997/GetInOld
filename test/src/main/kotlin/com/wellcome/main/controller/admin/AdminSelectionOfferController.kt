package com.wellcome.main.controller.admin

import com.wellcome.main.dto.admin.response.SelectionOfferResponse
import com.wellcome.main.dto.api.paths.QueryString
import com.wellcome.main.service.management.admin.AdminSelectionOfferManagementService
import com.wellcome.main.util.enumerators.Permissions
import com.wellcome.main.util.variables.Paths
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = [Paths.BASE_SELECTION_OFFER])
open class AdminSelectionOfferController @Autowired constructor(
    private val adminSelectionOfferManagementService: AdminSelectionOfferManagementService
) {

    @Secured(value = [Permissions.PermissionValues.SELECT_OFFER])
    @GetMapping(value = [Paths.SelectionOffer.SELECT])
    open fun selectOffer(@RequestParam(value = QueryString.OFFER_ID) offerId: Long,
                         @RequestParam(value = QueryString.SELECTION_ID) selectionId: Long) {
        adminSelectionOfferManagementService.selectOffer(offerId, selectionId)
    }

    @Secured(value = [Permissions.PermissionValues.GET_ALL_SELECTION_OFFERS])
    @GetMapping(value = [Paths.SelectionOffer.GET_ALL])
    open fun getAll(@RequestParam(value = QueryString.SELECTION_ID) selectionId: Long): SelectionOfferResponse {
        return adminSelectionOfferManagementService.getAll(selectionId)
    }

    @Secured(value = [Permissions.PermissionValues.REMOVE_SELECTION_OFFER])
    @DeleteMapping(value = [Paths.SelectionOffer.REMOVE])
    open fun remove(
        @RequestParam(value = QueryString.SELECTION_OFFER_ID) selectionOfferId: Long
    ) {
        adminSelectionOfferManagementService.remove(selectionOfferId)
    }
}