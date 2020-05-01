package com.wellcome.main.entity

import com.wellcome.main.entity.institution.Institution
import com.wellcome.main.entity.institution.InstitutionEvent
import com.wellcome.main.entity.user.User
import javax.persistence.*

@Entity
@Table(name = "bookmarks")
class Bookmark(

    @ManyToOne
    @JoinColumn(name = "institution_id")
    var institution: Institution? = null,

    @ManyToOne
    @JoinColumn(name = "event_id")
    var event: InstitutionEvent? = null,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Enumerated(value = EnumType.STRING)
    @Column(name = "type")
    var type: BookmarkType

) : BaseEntity()

enum class BookmarkType {
    INSTITUTION, EVENT
}