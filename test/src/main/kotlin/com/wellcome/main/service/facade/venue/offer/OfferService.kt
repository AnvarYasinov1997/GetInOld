package com.wellcome.main.service.facade.venue.offer

import com.wellcome.main.entity.venue.offer.Offer
import com.wellcome.main.repository.local.postgre.OfferRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

interface OfferService : BaseService<Offer>

@Service
open class DefaultOfferService @Autowired constructor(
    private val offerRepository: OfferRepository
) : DefaultBaseService<Offer>(Offer::class.java.simpleName, offerRepository), OfferService