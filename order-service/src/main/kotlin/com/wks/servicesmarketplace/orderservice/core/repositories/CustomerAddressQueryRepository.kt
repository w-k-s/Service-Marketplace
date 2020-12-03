package com.wks.servicesmarketplace.orderservice.core.repositories

import com.wks.servicesmarketplace.orderservice.core.models.serviceorder.entities.Address
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerAddressQueryRepository : CrudRepository<Address, Long>{
    fun findByExternalIdAndCustomerExternalId(addressExternalId: Long, customerExternalId: Long): Address?
}