package com.wks.servicesmarketplace.orderservice.core

interface ServiceOrderDao {
    fun save(serviceOrder: ServiceOrder)
    fun findById(orderUUID: OrderUUID): ServiceOrder?
    fun nextOrderId(): OrderId
}