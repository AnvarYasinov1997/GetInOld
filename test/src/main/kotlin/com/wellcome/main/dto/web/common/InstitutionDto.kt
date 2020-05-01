package com.wellcome.main.dto.web.common

data class InstitutionDto(val id: Long,
                          val name: String,
                          val description: String,
                          val rating: Double,
                          val avatarUrl: String,
                          val address: String,
                          val pictureUrls: List<String>,
                          val numberOfPeopleRated: Long,
                          val phones: List<String>,
                          val categories: List<CategoryDto>,
                          val lat: Double,
                          val lon: Double,
                          val rated: Boolean,
                          val distance: Int?,
                          val worksUp: WorksUpDto,
                          val saved: Boolean)