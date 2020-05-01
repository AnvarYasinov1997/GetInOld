package com.wellcome.main.service.facade.venue.offer

import com.wellcome.main.entity.venue.offer.OfferContent
import com.wellcome.main.repository.local.postgre.OfferContentRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

interface OfferContentService : BaseService<OfferContent>

@Service
open class DefaultOfferContentService @Autowired constructor(
    private val offerContentRepository: OfferContentRepository
) : DefaultBaseService<OfferContent>(OfferContent::class.java.simpleName, offerContentRepository),
    OfferContentService