package com.wks.servicesmarketplace.orderservice.core.repositories

import com.wks.servicesmarketplace.orderservice.core.models.serviceorder.entities.Address
import com.wks.servicesmarketplace.orderservice.core.models.serviceorder.entities.AddressId
import com.wks.servicesmarketplace.orderservice.core.models.serviceorder.entities.CustomerId
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerAddressQueryRepository : CrudRepository<Address, AddressId>{
    fun findByExternalIdAndCustomerExternalId(addressExternalId: AddressId, customerExternalId: CustomerId): Address?
}