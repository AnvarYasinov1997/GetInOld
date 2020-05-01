package com.wellcome.main.entity.selection

import com.wellcome.main.entity.BaseEntity
import com.wellcome.main.entity.institution.InstitutionOffer
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "selections_offers")
class SelectionOffer(

    @ManyToOne
    @JoinColumn(name = "selection_id")
    var selection: Selection,

    @ManyToOne
    @JoinColumn(name = "offer_id", nullable = false, unique = true)
    var offer: InstitutionOffer

) : BaseEntity()