package com.wellcome.main.entity

import javax.persistence.*

@Entity
@Table(name = "instructions")
class Instruction(

    @Enumerated(EnumType.STRING)
    @Column(name = "background_color", nullable = false)
    var backgroundColor: BackgroundColor,

    @Column(name = "title")
    var title: String,

    @Column(name = "text", nullable = false)
    var text: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var type: InstructionType,

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type")
    var actionType: InstructionActionType

) : BaseEntity()

enum class BackgroundColor(val hexCode: String) {
    WHITE("0xFFFFFFFF"),
    ACCENT("0xFFFFB84E"),
    BACKGROUND("0xFFf6f6f6"),
    DARK_BACKGROUND("0xFFe5e5e5"),
    DEEP_PURPLE("0xFF7F74FF"),
    PRIMARY_TEXT("0xFF3e3e3e"),
    SECONDARY_TEXT("0xFF808080"),
    FAT_GRAY("0xFF606060"),
    YELLOW("0xFFFFD231"),
    RED("0xFFFF7474"),
    BLACK("0xFF000000"),
    TRANSPARENT_BLACK("0xCC000000")
}

enum class InstructionType {
    HELP, BIRTHDAY, OFFER, EVENT
}

enum class InstructionActionType {
    NONE, CREATE_BIRTHDAY_CAMPAIGN
}