package com.wks.servicesmarketplace.orderservice.core

import com.wks.servicemarketplace.common.CompanyUUID
import java.time.Clock
import java.time.OffsetDateTime
import javax.money.MonetaryAmount

data class Bid(
        val uuid: BidUUID,
        val orderUUID: OrderUUID,
        val companyUUID: CompanyUUID,
        val price: MonetaryAmount,
        val note: String,
        val createdBy: String,
        val createdDate: OffsetDateTime = OffsetDateTime.now(Clock.systemUTC()),
        val lastModifiedBy: String? = null,
        val lastModifiedDate: OffsetDateTime? = null,
        val version: Int = 0
)