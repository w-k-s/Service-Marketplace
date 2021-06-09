package com.wks.servicesmarketplace.orderservice.core

import com.wks.servicemarketplace.common.CompanyId
import java.time.Clock
import java.time.OffsetDateTime
import javax.money.MonetaryAmount

data class Quote(
        val id: QuoteId,
        val uuid: QuoteUUID,
        val orderId: OrderId,
        val companyId: CompanyId,
        val price: MonetaryAmount,
        val note: String,
        val createdBy: String,
        val createdDate: OffsetDateTime = OffsetDateTime.now(Clock.systemUTC()),
        val lastModifiedBy: String? = null,
        val lastModifiedDate: OffsetDateTime? = null,
        val version: Int = 0
)