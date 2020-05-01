package com.wellcome.main.service.facade.venue.offer

import com.wellcome.main.entity.venue.offer.OfferWorkTime
import com.wellcome.main.repository.local.postgre.OfferWorkTimeRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

interface OfferWorkTimeService : BaseService<OfferWorkTime>

@Service
open class DefaultOfferWorkTimeService @Autowired constructor(
    private val offerWorkTimeRepository: OfferWorkTimeRepository
) : DefaultBaseService<OfferWorkTime>(OfferWorkTime::class.java.simpleName, offerWorkTimeRepository),
    OfferWorkTimeService