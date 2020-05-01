package com.wellcome.main.entity.institution

import com.wellcome.main.entity.BaseEntity
import javax.persistence.*

@Entity
@Table(name = "institution_categories")
class InstitutionCategory(

    @Column(name = "title", nullable = false)
    var title: String,

    @Column(name = "ranging", nullable = false)
    var ranging: Boolean,

    @Enumerated(value = EnumType.STRING)
    @Column(name = "category_type", nullable = false, unique = true)
    var categoryType: InstitutionCategoryType

) : BaseEntity()

enum class InstitutionCategoryType {
    HOOKAH,
    NIGHT_CLUB,
    STRIP_BAR,
    KARAOKE,
    PUB,
    BAR,
    LOUNGE_BAR,
    RESTAURANT,
    COFFEE_HOUSE,
    VAPE_BAR;

    fun getOfferTypes(): List<OfferType> {
        return when (this) {
            HOOKAH -> listOf(OfferType.HOOKAH)
            NIGHT_CLUB -> listOf(OfferType.WHISKEY, OfferType.VODKA, OfferType.COCKTAIL)
            STRIP_BAR -> listOf(OfferType.WHISKEY, OfferType.VODKA, OfferType.COCKTAIL)
            KARAOKE -> listOf(OfferType.WHISKEY, OfferType.VODKA, OfferType.COCKTAIL)
            PUB -> listOf(OfferType.BEER)
            BAR -> listOf(OfferType.WHISKEY, OfferType.VODKA, OfferType.COCKTAIL, OfferType.BEER)
            LOUNGE_BAR -> listOf(OfferType.HOOKAH, OfferType.MENU)
            RESTAURANT -> listOf(OfferType.MENU)
            COFFEE_HOUSE -> listOf(OfferType.HOOKAH, OfferType.MENU)
            VAPE_BAR -> listOf(OfferType.HOOKAH)
        }
    }

    fun compareCategories(vararg categories: InstitutionCategoryType): Boolean {
        for (i in categories) {
            if (this == i) return true
        }
        return false
    }

    companion object {
        fun getRangingSequence(): List<InstitutionCategoryType> =
            listOf(PUB, BAR, KARAOKE, HOOKAH, NIGHT_CLUB, LOUNGE_BAR, STRIP_BAR, VAPE_BAR, COFFEE_HOUSE, RESTAURANT)
    }

}