package com.wks.servicesmarketplace.orderservice.core.repositories

import com.wks.servicesmarketplace.orderservice.core.OrderUUID
import com.wks.servicesmarketplace.orderservice.core.ServiceOrder
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ServiceOrderRepository : CrudRepository<ServiceOrder, OrderUUID>