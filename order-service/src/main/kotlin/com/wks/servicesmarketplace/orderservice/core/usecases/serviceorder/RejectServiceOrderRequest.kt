package com.wks.servicesmarketplace.orderservice.core.usecases.serviceorder

data class RejectServiceOrderRequest(val orderId: String?,
                                     val rejectReason: String?)