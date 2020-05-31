package com.wks.servicesmarketplace.jobservice.core.exceptions

data class ServiceOrderNotFoundException(val orderId: String) : Exception(orderId)