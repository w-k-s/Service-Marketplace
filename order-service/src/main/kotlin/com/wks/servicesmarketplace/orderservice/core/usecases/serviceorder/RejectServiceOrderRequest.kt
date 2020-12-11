package com.wks.servicesmarketplace.orderservice.core.usecases.serviceorder

import com.wks.servicesmarketplace.orderservice.core.models.serviceorder.entities.OrderUUID

data class RejectServiceOrderRequest(val orderId: OrderUUID?,
                                     val rejectReason: String?)