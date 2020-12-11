package com.wks.servicesmarketplace.orderservice.core.models.serviceorder.entities

import java.math.BigDecimal
import javax.persistence.*

@Entity
@Table(name = "address")
data class Address(
        @field:EmbeddedId
        @AttributeOverride(name = "value", column = Column(name = "external_id"))
        val externalId: AddressId,
        @AttributeOverride(name = "value", column = Column(name = "customer_external_id"))
        val customerExternalId: CustomerId,
        val name: String,
        val line1: String,
        val line2: String?,
        val city: String,
        val country: String,
        @field:Column(name = "latitude", precision = 9, scale = 5) val latitude: BigDecimal,
        @field:Column(name = "longitude", precision = 9, scale = 5) val longitude: BigDecimal,
        val version: Long
)

data class AddressAddedEvent(
        val externalId: AddressId,
        val customerExternalId: CustomerId,
        val name: String,
        val line1: String,
        val line2: String?,
        val city: String,
        val country: String,
        val latitude: BigDecimal,
        val longitude: BigDecimal,
        val createdBy: String,
        val version: Long
)