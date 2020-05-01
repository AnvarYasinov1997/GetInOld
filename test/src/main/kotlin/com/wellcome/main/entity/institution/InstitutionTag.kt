package com.wellcome.main.entity.institution

import com.wellcome.main.entity.BaseEntity
import javax.persistence.*

@Entity
@Table(name = "institution_tags")
class InstitutionTag(

    @Column(name = "name")
    var name: String,

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    var institution: Institution

) : BaseEntity()

enum class InstitutionTagType {
    PARKING,
    CARD_PAY,
    DRESS_CODE,
    SUMMER_TERRACE,
    LIVE_MUSIC,
    SMOKING_ROOM,
    NON_SMOKING_ROOM,
    BILLIARDS,
    GAME_CONSOLE,
    BEER_PONG,
    TABLE_GAMES,
    PLAYGROUND;
}