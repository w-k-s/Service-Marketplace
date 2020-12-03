package com.wks.servicesmarketplace.orderservice.core.models.serviceorder.entities

import org.hibernate.validator.constraints.Length
import java.math.BigDecimal
import javax.persistence.Embeddable
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.NotBlank

@Embeddable
class Money {
    private val amount: @DecimalMin(value = "0.0") BigDecimal?
    private val currency: @NotBlank @Length(min = 3, max = 3) String?

    constructor() {
        amount = BigDecimal.ZERO
        currency = "---"
    }

    constructor(amount: BigDecimal?, currency: String?) {
        if (amount == null) throw NullPointerException("amount")
        require(!(currency == null || currency.trim(' ').isEmpty())) { "Invalid currency" }
        this.amount = amount
        this.currency = currency
    }

    override fun toString(): String {
        return String.format("%s %s", currency, amount)
    }
}