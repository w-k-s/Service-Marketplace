package com.wks.servicesmarketplace.orderservice.core

import com.wks.servicemarketplace.common.CompanyUUID

interface BidDao {
    fun nextBidId(): BidId
    fun findByCompanyUUID(companyUUID: CompanyUUID): Bid?
    fun save(bid: Bid) : Boolean
    fun update(bid: BidId, version: Int, newBid: Bid): Boolean
}