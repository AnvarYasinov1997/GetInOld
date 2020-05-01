package com.wellcome.main.entity.venue

import com.wellcome.main.entity.BaseEntity
import com.wellcome.main.entity.institution.OfferType
import javax.persistence.*

@Entity
@Table(name = "categories")
class Category(

    @Enumerated(value = EnumType.STRING)
    @Column(name = "type", nullable = false, unique = true)
    var type: CategoryType

) : BaseEntity()

enum class CategoryType {
    HOOKAH,
    NIGHT_CLUB,
    STRIP_BAR,
    KARAOKE,
    PUB,
    BAR,
    LOUNGE_BAR,
    RESTAURANT,
    COFFEE_HOUSE;

    fun getOfferTypes(): List<OfferType> {
        return when (this) {
            HOOKAH -> listOf(OfferType.HOOKAH, OfferType.COFFEE)
            NIGHT_CLUB -> listOf(OfferType.WHISKEY, OfferType.VODKA, OfferType.COCKTAIL, OfferType.ALCOHOL)
            STRIP_BAR -> listOf(OfferType.WHISKEY, OfferType.VODKA, OfferType.COCKTAIL, OfferType.ALCOHOL)
            KARAOKE -> listOf(OfferType.WHISKEY, OfferType.VODKA, OfferType.COCKTAIL)
            PUB -> listOf(OfferType.BEER)
            BAR -> listOf(OfferType.WHISKEY, OfferType.VODKA, OfferType.COCKTAIL, OfferType.BEER, OfferType.ALCOHOL)
            LOUNGE_BAR -> listOf(OfferType.HOOKAH, OfferType.MENU, OfferType.VINE, OfferType.COFFEE)
            RESTAURANT -> listOf(OfferType.MENU, OfferType.VINE)
            COFFEE_HOUSE -> listOf(OfferType.HOOKAH, OfferType.MENU, OfferType.COFFEE)
        }
    }

}