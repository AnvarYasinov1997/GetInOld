package com.wellcome.main.dto.admin.request

data class MarketingRequest(val id: Long,
                            val title: String,
                            val text: String,
                            val pictureUrl: String)