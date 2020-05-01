package com.wellcome.main.service.facade.venue

import com.wellcome.main.entity.venue.VenueWorkTime
import com.wellcome.main.repository.local.postgre.VenueWorkTimeRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

interface VenueWorkTimeService : BaseService<VenueWorkTime>

@Service
open class DefaultVenueWorkTimeService @Autowired constructor(
    private val venueWorkTimeRepository: VenueWorkTimeRepository
) : DefaultBaseService<VenueWorkTime>(VenueWorkTime::class.java.simpleName, venueWorkTimeRepository),
    VenueWorkTimeService