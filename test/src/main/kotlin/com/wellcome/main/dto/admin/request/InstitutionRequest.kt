package com.wellcome.main.dto.admin.request

import com.wellcome.main.dto.admin.common.*
import com.wellcome.main.dto.admin.response.PhoneDto

data class AddInstitutionRequest(val name: String,
                                 val institutionId: Long,
                                 val avatarUrl: String,
                                 val description: String,
                                 val comments: String,
                                 val rating: Double,
                                 val numberOfPeopleRated: Long,
                                 val lat: Double,
                                 val lon: Double,
                                 val address: String,
                                 val instagram: String,
                                 val newTags: List<TagDto>,
                                 val newCategories: List<Long>,
                                 val newPictureUrls: List<String>,
                                 val newOffers: List<OfferDto>,
                                 val newEvents: List<EventDto>,
                                 val newPhones: List<PhoneDto>,
                                 val worksUp: List<WorksUpDto>,
                                 val blocked: Boolean)

data class EditInstitutionRequest(val name: String,
                                  val institutionId: Long,
                                  val avatarUrl: String,
                                  val description: String,
                                  val comments: String,
                                  val newTags: List<TagDto>,
                                  val removeTags: List<Long>,
                                  val newCategories: List<Long>,
                                  val removeCategories: List<Long>,
                                  val address: String,
                                  val instagram: String,
                                  val newPictureUrls: List<String>,
                                  val removePictureIds: List<Long>,
                                  val newOffers: List<OfferDto>,
                                  val removedOfferIds: List<Long>,
                                  val newEvents: List<EventDto>,
                                  val removedEventIds: List<Long>,
                                  val newPhones: List<PhoneDto>,
                                  val removedPhoneIds: List<Long>,
                                  val worksUp: List<WorksUpDto>,
                                  val rating: Double,
                                  val numberOfPeopleRated: Long,
                                  val blocked: Boolean,
                                  val editRequestFeedbackDto: InstitutionEditRequestFeedbackDto?)

data class CustomizeInstitutionRequest(val avatarUrl: String,
                                       val name: String,
                                       val newInstagram: String,
                                       val description: String,
                                       val tags: List<Long>,
                                       val removeTags: List<Long>,
                                       val pictureUrls: List<String>,
                                       val removePictures: List<Long>)
