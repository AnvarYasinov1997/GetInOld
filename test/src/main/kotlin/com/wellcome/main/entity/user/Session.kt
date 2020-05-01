package com.wellcome.main.entity.user

import com.wellcome.main.entity.BaseEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "sessions")
class Session(

    @Column(name = "instance_id", nullable = false, unique = true)
    var instanceId: String,

    @Column(name = "fcm_token", nullable = false, unique = true)
    var fcmToken: String,

    @Column(name = "latest_session", nullable = false)
    var latestSession: String,

    @Column(name = "latest_push_notification")
    var latestPushNotification: String? = null

) : BaseEntity()