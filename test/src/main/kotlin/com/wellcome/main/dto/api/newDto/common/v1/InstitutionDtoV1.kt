package com.wellcome.main.dto.api.newDto.common.v1

data class InstitutionDtoV1(val id: Long,
                            val name: String,
                            val description: String,
                            val rating: Double,
                            val avatarUrl: String,
                            val address: String,
                            val pictureUrls: List<String>,
                            val numberOfPeopleRated: Long,
                            val phones: List<String>,
                            val categories: List<CategoryDtoV1>,
                            val lat: Double,
                            val lon: Double,
                            val rated: Boolean,
                            val distance: Int?,
                            val worksUp: WorksUpDtoV1,
                            val saved: Boolean)

data class BlockInstitutionsDtoV1(val title: String,
                                  val institutions: List<InstitutionDtoV1>)