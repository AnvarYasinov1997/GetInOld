package com.wellcome.main.service.facade

import com.wellcome.main.entity.Price
import com.wellcome.main.repository.local.postgre.PriceRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

interface PriceService : BaseService<Price>

@Service
open class DefaultPriceService @Autowired constructor(
    private val priceRepository: PriceRepository
) : PriceService, DefaultBaseService<Price>(Price::class.java.simpleName, priceRepository)