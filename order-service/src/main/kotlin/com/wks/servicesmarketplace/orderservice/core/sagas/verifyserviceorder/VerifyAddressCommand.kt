package com.wks.servicesmarketplace.orderservice.core.sagas.verifyserviceorder

import com.wks.servicesmarketplace.orderservice.core.models.serviceorder.entities.AddressId
import com.wks.servicesmarketplace.orderservice.core.models.serviceorder.entities.CustomerId
import com.wks.servicesmarketplace.orderservice.core.models.serviceorder.entities.OrderUUID
import java.math.BigDecimal

data class VerifyAddressCommand(
        val orderId: OrderUUID,
        val customerExternalId: CustomerId,
        val addressExternalId: AddressId,
        val addressLatitude: BigDecimal,
        val addressLongitude: BigDecimal,
        val addressVersion: Long
)
