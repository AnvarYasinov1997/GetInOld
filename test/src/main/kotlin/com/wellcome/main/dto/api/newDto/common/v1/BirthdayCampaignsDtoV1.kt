package com.wellcome.main.dto.api.newDto.common.v1

data class BirthdayCampaignsDtoV1(val id: Long,
                                  val userDto: UserDtoV1,
                                  val showBirthdayCampaignsFullScreen: Boolean,
                                  val birthdayCampaignsDtoList: List<BirthdayCampaignDtoV1>)