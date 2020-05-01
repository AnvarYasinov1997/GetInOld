package com.wellcome.main.service.management.admin

import com.wellcome.main.dto.admin.common.MarketingDto
import com.wellcome.main.dto.admin.request.MarketingRequest
import com.wellcome.main.dto.admin.response.MarketingResponse
import com.wellcome.main.entity.Marketing
import com.wellcome.main.service.facade.MarketingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface AdminMarketingManagementService {
    fun add(request: MarketingRequest)
    fun getAll(): MarketingResponse
}

@Service
open class DefaultAdminMarketingManagementService @Autowired constructor(
    private val marketingService: MarketingService
) : AdminMarketingManagementService {

    @Transactional
    override fun add(request: MarketingRequest) {
        Marketing(
            title = request.title,
            text = request.text,
            pictureUrl = request.pictureUrl
        ).let(marketingService::saveOrUpdate)
    }

    @Transactional(readOnly = true)
    override fun getAll(): MarketingResponse {
        return marketingService.findAll().map {
            MarketingDto(it.id!!, it.title, it.text, it.pictureUrl)
        }.let(::MarketingResponse)
    }
}