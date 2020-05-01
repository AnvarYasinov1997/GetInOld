package com.wellcome.main.service.facade

import com.wellcome.main.entity.Marketing
import com.wellcome.main.repository.local.postgre.MarketingRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

interface MarketingService : BaseService<Marketing>

@Service
open class DefaultMarketingService @Autowired constructor(
    private val marketingRepository: MarketingRepository
): DefaultBaseService<Marketing>(Marketing::class.java.simpleName, marketingRepository),
    MarketingService