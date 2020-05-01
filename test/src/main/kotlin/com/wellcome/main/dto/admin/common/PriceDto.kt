package com.wellcome.main.dto.admin.common

data class PriceDto(val lowerAmount: Double,
                    val topAmount: Double,
                    val fixAmount: Double,
                    val free: Boolean,
                    val fixPrice: Boolean,
                    val currencyType: String)