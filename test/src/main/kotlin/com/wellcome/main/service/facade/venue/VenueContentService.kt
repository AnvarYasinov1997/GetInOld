package com.wellcome.main.service.facade.venue

import com.wellcome.main.entity.venue.VenueContent
import com.wellcome.main.repository.local.postgre.VenueContentRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

interface VenueContentService : BaseService<VenueContent>

@Service
open class DefaultVenueContentService @Autowired constructor(
    private val venueContentRepository: VenueContentRepository
) : DefaultBaseService<VenueContent>(VenueContent::class.java.simpleName, venueContentRepository),
    VenueContentService