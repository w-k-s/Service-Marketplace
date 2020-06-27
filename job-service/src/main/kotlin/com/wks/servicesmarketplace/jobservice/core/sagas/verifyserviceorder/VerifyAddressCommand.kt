package com.wks.servicesmarketplace.jobservice.core.sagas.verifyserviceorder

import java.math.BigDecimal

data class VerifyAddressCommand(
        val orderId: String,
        val customerExternalId: Long,
        val addressExternalId: Long,
        val addressLatitude: BigDecimal,
        val addressLongitude: BigDecimal,
        val addressVersion: Long
)
