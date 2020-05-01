package com.wellcome.main.entity

import java.math.BigDecimal
import javax.persistence.*

@Entity
@Table(name = "prices")
class Price(

    @Column(name = "lower_amount", nullable = false)
    var lowerAmount: BigDecimal,

    @Column(name = "top_amount", nullable = false)
    var topAmount: BigDecimal,

    @Column(name = "fix_amount", nullable = false)
    var fixAmount: BigDecimal,

    @Column(name = "free", nullable = false)
    var free: Boolean,

    @Column(name = "fix_price", nullable = false)
    var fixPrice: Boolean,

    @Enumerated(value = EnumType.STRING)
    @Column(name = "currency_type", nullable = false)
    var currencyType: CurrencyType

) : BaseEntity() {

    fun toPriceString(): String {
        if (free) return "Бесплатно"
        if (fixPrice) return "${this.fixAmount}${this.currencyType.name}"
        return "${this.lowerAmount}-${this.topAmount}${this.currencyType.name}"
    }

}

enum class CurrencyType {
    KGS
}