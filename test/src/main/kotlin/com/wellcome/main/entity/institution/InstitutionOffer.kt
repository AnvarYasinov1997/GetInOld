package com.wellcome.main.entity.institution

import com.wellcome.main.dto.api.newDto.Days
import com.wellcome.main.dto.api.newDto.common.v1.BlockType
import com.wellcome.main.entity.BaseEntity
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption
import javax.persistence.*

@Entity
@Table(name = "institution_offers")
class InstitutionOffer(

    @ManyToOne
    @JoinColumn(name = "institution_id")
    var institution: Institution?,

    @Column(name = "title", nullable = false)
    var title: String,

    @Column(name = "text", nullable = false)
    var text: String,

    @Enumerated(value = EnumType.STRING)
    @Column(name = "offer_type", nullable = false)
    var offerType: OfferType,

    @Column(name = "picture_url", nullable = false)
    var pictureUrl: String,

    @Column(name = "start_date", nullable = false)
    var startDate: String,

    @Column(name = "end_date", nullable = false)
    var endDate: String,

    @Column(name = "birthday", nullable = false)
    var birthday: Boolean,

    @Column(name = "completed", nullable = false)
    var completed: Boolean,

    @Column(name = "active", nullable = false)
    var active: Boolean,

    @Column(name = "promoted", nullable = false)
    var promoted: Boolean = false,

    @Column(name = "in_review", nullable = false)
    var inReview: Boolean,

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "institutionOffer")
    var workTime: MutableList<InstitutionOfferWorkTime> = mutableListOf()

) : BaseEntity() {

    fun getInstitutionNotNull(): Institution = requireNotNull(this.institution)

    fun getInstitutionRatingNotNull(): Double = requireNotNull(this.institution).rating

}

enum class OfferType {
    HOOKAH, BEER, VODKA, WHISKEY, COCKTAIL, KARAOKE, MENU, OTHER, VINE, COFFEE, ALCOHOL;

    fun checkOfferType(offers: List<OfferType>): Boolean {
        for (i in offers) {
            if (this == i) {
                return true
            }
        }
        return false
    }

    fun toOfferTitle(): String {
        return when (this) {
            HOOKAH -> "Акции на кальян"
            BEER -> "Акции на пиво"
            VODKA -> "Акции на водку"
            WHISKEY -> "Акции на виски"
            COCKTAIL -> "Акции на коктейли"
            KARAOKE -> "Акции на караоке"
            MENU -> "Акции на меню"
            OTHER -> "Другие акции"
            else -> "Другие акции"
        }
    }

    fun toAllOfferTitle(day: Days): String {
        val title = when (this) {
            HOOKAH -> "Все акции на кальян"
            BEER -> "Все акции на пиво"
            VODKA -> "Все акции на водку"
            WHISKEY -> "Все акции на виски"
            COCKTAIL -> "Все акции на коктейли"
            KARAOKE -> "Все акции на караоке"
            MENU -> "Все акции на меню"
            OTHER -> "Все другие акции"
            else -> "Все другие акции"
        }
        return title.plus(day.getEnding())
    }

    fun toBlockType(): BlockType {
        return when (this) {
            HOOKAH -> BlockType.OFFER_HOOKAH
            BEER -> BlockType.OFFER_BEER
            VODKA -> BlockType.OFFER_VODKA
            WHISKEY -> BlockType.OFFER_WHISKEY
            COCKTAIL -> BlockType.OFFER_COCKTAIL
            KARAOKE -> BlockType.OFFER_KARAOKE
            MENU -> BlockType.OFFER_MENU
            OTHER -> BlockType.OFFER_OTHER
            VINE -> BlockType.OFFER_VINE
            COFFEE -> BlockType.OFFER_COFFEE
            ALCOHOL -> BlockType.OFFER_ALCOHOL
        }
    }

}