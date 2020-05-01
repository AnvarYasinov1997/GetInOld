package com.wellcome.main.entity.institutionProfile

import com.wellcome.main.entity.BaseEntity
import javax.persistence.*

@Entity
@Table(name = "institution_edit_request_statuses")
class InstitutionEditRequestStatus(

    @Column(name = "developer_message", nullable = false)
    var developerMessage: String = "",

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    var status: InstitutionEditRequestFeedBackStatus =
        InstitutionEditRequestFeedBackStatus.IN_REVIEW

) : BaseEntity()

enum class InstitutionEditRequestFeedBackStatus {
    APPROVED, KICKBACK, IN_REVIEW
}