package com.wellcome.main.dto.api.newDto.common.v1

import com.wellcome.main.entity.institution.OfferType

data class OfferDtoV1(val id: Long,
                      val title: String,
                      val description: String,
                      val type: String,
                      val photoUrl: String,
                      val institutionDto: InstitutionDtoV1)

data class BlockOffersDtoV1(val title: String,
                            val blockType: String,
                            val offers: List<OfferDtoV1>,
                            val showAll: Boolean)

enum class BlockType {
    MOCK,

    DYNAMIC_SEARCH_OFFERS,

    FEED_OFFERS_INTERESTING,
    FEED_OFFERS,

    OFFER_BEER,
    OFFER_HOOKAH,
    OFFER_COCKTAIL,
    OFFER_KARAOKE,
    OFFER_VODKA,
    OFFER_WHISKEY,
    OFFER_MENU,
    OFFER_VINE,
    OFFER_COFFEE,
    OFFER_ALCOHOL,
    OFFER_OTHER,

    OFFER_BY_SEARCH_OFFERS,

    INSTITUTION_PROFILE_OFFERS,
    INSTITUTION_PROFILE_REVIEWS;

    fun toOfferType(): OfferType {
        return when (this) {
            OFFER_HOOKAH -> OfferType.HOOKAH
            OFFER_BEER -> OfferType.BEER
            OFFER_VODKA -> OfferType.VODKA
            OFFER_WHISKEY -> OfferType.WHISKEY
            OFFER_COCKTAIL -> OfferType.COCKTAIL
            OFFER_KARAOKE -> OfferType.KARAOKE
            OFFER_MENU -> OfferType.MENU
            OFFER_OTHER -> OfferType.OTHER
            OFFER_VINE -> OfferType.VINE
            OFFER_COFFEE -> OfferType.COFFEE
            OFFER_ALCOHOL -> OfferType.ALCOHOL
            else -> OfferType.OTHER
        }
    }

    fun isOfferType(): Boolean {
        return when (this) {
            OFFER_BEER -> true
            OFFER_HOOKAH -> true
            OFFER_COCKTAIL -> true
            OFFER_KARAOKE -> true
            OFFER_VODKA -> true
            OFFER_WHISKEY -> true
            OFFER_MENU -> true
            OFFER_OTHER -> true
            OFFER_VINE -> true
            OFFER_ALCOHOL -> true
            OFFER_COFFEE -> true
            else -> false
        }
    }

}