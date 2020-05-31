package com.wks.servicesmarketplace.jobservice.core.usecases.serviceorder

data class RejectServiceOrderRequest(val orderId: String?,
                                     val rejectReason: String?)