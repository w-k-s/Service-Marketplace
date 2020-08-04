package com.wks.servicesmarketplace.jobservice.core.usecases.serviceorder

import com.wks.servicesmarketplace.jobservice.core.auth.User

data class ServiceOrderRequest(
        val customerExternalId: Long,
        val addressExternalId: Long,
        val serviceCategoryId: Long,
        val title: String,
        val description: String,
        val orderDateTime: String,
        val user: User
)
