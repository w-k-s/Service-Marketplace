package com.wks.servicesmarketplace.jobservice.core.models.serviceorder.entities

import java.math.BigDecimal
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "address")
data class Address(
        @field:Id val externalId: Long,
        val customerExternalId: Long,
        val name: String,
        val line1: String,
        val line2: String?,
        val city: String,
        val country: String,
        @field:Column(name = "latitude", precision = 9, scale = 5) val latitude: BigDecimal,
        @field:Column(name = "longitude", precision = 9, scale = 5) val longitude: BigDecimal,
        val version: Long
)