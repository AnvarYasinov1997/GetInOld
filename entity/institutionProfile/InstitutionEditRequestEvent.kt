package com.wellcome.main.entity.institutionProfile

import com.wellcome.main.entity.BaseEntity
import com.wellcome.main.entity.institution.InstitutionEvent
import javax.persistence.*

@Entity
@Table(name = "institution_edit_request_events")
class InstitutionEditRequestEvent(

    @OneToOne
    @JoinColumn(name = "institution_event_id", nullable = false, unique = true)
    var event: InstitutionEvent,

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    var status: InstitutionEditRequestContentStatus,

    @ManyToOne
    @JoinColumn(name = "institution_edit_request_id", nullable = false)
    var institutionEditRequest: InstitutionEditRequest

) : BaseEntity()