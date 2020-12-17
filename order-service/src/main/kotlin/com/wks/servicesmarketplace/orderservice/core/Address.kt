package com.wks.servicesmarketplace.orderservice.core

import java.math.BigDecimal
import javax.persistence.*

@Entity
@Table(name = "address")
data class Address(
        @field:EmbeddedId
        @field:AttributeOverride(name = "value", column = Column(name = "order_id"))
        val orderUUID: OrderUUID,
        val line1: String,
        val line2: String?,
        val city: String,
        @field:Embedded
        val country: CountryCode,
        @field:Column(name = "latitude", precision = 9, scale = 5)
        val latitude: BigDecimal,
        @field:Column(name = "longitude", precision = 9, scale = 5)
        val longitude: BigDecimal
)