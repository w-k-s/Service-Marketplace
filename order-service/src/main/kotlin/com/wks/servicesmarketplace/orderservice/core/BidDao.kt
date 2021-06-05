package com.wks.servicesmarketplace.orderservice.core

import com.wks.servicemarketplace.common.CompanyUUID

interface BidDao {
    fun findByCompanyId(companyUUID: CompanyUUID): Bid?
    fun save(bid: Bid) : Boolean
    fun update(bid: BidUUID, version: Int, newBid: Bid): Boolean
}