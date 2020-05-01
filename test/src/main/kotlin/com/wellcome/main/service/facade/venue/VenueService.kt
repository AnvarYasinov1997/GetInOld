package com.wellcome.main.service.facade.venue

import com.wellcome.main.entity.venue.Venue
import com.wellcome.main.repository.local.postgre.VenueRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

interface VenueService : BaseService<Venue>

@Service
open class DefaultVenueService @Autowired constructor(
    private val venueRepository: VenueRepository
) : DefaultBaseService<Venue>(Venue::class.java.simpleName, venueRepository), VenueService