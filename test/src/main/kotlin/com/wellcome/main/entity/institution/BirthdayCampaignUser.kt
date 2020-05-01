package com.wellcome.main.entity.institution

import com.wellcome.main.entity.BaseEntity
import com.wellcome.main.entity.user.User
import javax.persistence.*

@Entity
@Table(name = "birthday_campaigns_users")
class BirthdayCampaignUser(

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Column(name = "viewed", nullable = false)
    var viewed: Boolean = false,

    @Column(name = "expired", nullable = false)
    var expired: Boolean = false,

    @ManyToMany
    @JoinTable(
        name = "birthday_campaign_user_birthday_campaigns",
        joinColumns = [(JoinColumn(name = "birthday_campaign_user_id", referencedColumnName = "id"))],
        inverseJoinColumns = [(JoinColumn(name = "birthday_campaign_id", referencedColumnName = "id"))]
    )
    var birthdayCampaigns: MutableSet<BirthdayCampaign> = mutableSetOf()

): BaseEntity()