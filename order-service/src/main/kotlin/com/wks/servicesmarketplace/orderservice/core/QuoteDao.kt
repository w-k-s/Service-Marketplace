package com.wks.servicesmarketplace.orderservice.core

import com.wks.servicemarketplace.common.CompanyUUID

interface QuoteDao {
    fun nextQuoteId(): QuoteId
    fun findByCompanyUUID(companyUUID: CompanyUUID): Quote?
    fun save(quote: Quote) : Boolean
    fun update(quoteId: QuoteId, version: Int, newQuote: Quote): Boolean
}