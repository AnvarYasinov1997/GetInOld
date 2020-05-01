package com.wellcome.main.dto.moderation.response.v1

import com.wellcome.main.dto.moderation.common.v1.AnalyticDtoV1
import com.wellcome.main.dto.moderation.common.v1.InstructionDtoV1
import com.wellcome.main.dto.moderation.common.v1.MarketingDtoV1

data class DashboardResponseV1(val latestFetch: String,
                               val saved: Long,
                               val reviews: Long,
                               val shared: Long,
                               val instructionDtoList: List<InstructionDtoV1>,
                               val marketingDtoList: List<MarketingDtoV1>,
                               val coldAnalyticDto: AnalyticDtoV1,
                               val hotAnalyticDto: AnalyticDtoV1)