package com.wellcome.main.dto.web.common

data class BirthdayCampaignsDto(val id: Long,
                                val userDto: UserDto,
                                val showBirthdayCampaignsFullScreen: Boolean,
                                val birthdayCampaignsDtoList: List<BirthdayCampaignDto>)