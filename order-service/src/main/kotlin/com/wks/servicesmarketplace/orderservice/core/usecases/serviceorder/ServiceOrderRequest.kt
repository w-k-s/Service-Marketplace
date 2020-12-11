package com.wks.servicesmarketplace.orderservice.core.usecases.serviceorder

import com.wks.servicesmarketplace.orderservice.core.auth.Authentication
import com.wks.servicesmarketplace.orderservice.core.models.serviceorder.commands.constraints.datetime.ServiceOrderDateTime
import com.wks.servicesmarketplace.orderservice.core.models.serviceorder.entities.AddressId
import com.wks.servicesmarketplace.orderservice.core.models.serviceorder.entities.CustomerId
import com.wks.servicesmarketplace.orderservice.core.utils.ModelValidator
import java.time.OffsetDateTime
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class ServiceOrderRequest constructor(
        val customerExternalId: CustomerId,
        val addressExternalId: AddressId,
        val serviceCode: String,
        val title: String,
        val description: String,
        val orderDateTime: OffsetDateTime,
        val authentication: Authentication
) {
    class Builder {
        @field:NotNull
        var customerExternalId: Long? = null

        @field:NotNull
        var addressExternalId: Long? = null

        @field:NotBlank
        var serviceCode: String? = null

        @field:NotBlank
        var title: String? = null

        @field:NotBlank
        var description: String? = null

        @field:NotNull
        @field:ServiceOrderDateTime
        var orderDateTime: OffsetDateTime? = null

        @field:NotNull
        var authentication: Authentication? = null

        fun customerExternalId(customerExternalId: Long?): Builder {
            this.customerExternalId = customerExternalId
            return this
        }

        fun addressExternalId(addressExternalId: Long?): Builder {
            this.addressExternalId = addressExternalId
            return this
        }

        fun serviceCode(serviceCode: String?): Builder {
            this.serviceCode = serviceCode
            return this
        }

        fun title(title: String?): Builder {
            this.title = title
            return this
        }

        fun description(description: String?): Builder {
            this.description = description
            return this
        }

        fun orderDateTime(orderDateTime: OffsetDateTime?): Builder {
            this.orderDateTime = orderDateTime
            return this
        }

        fun authentication(authentication: Authentication?): Builder {
            this.authentication = authentication
            return this
        }

        fun build(): ServiceOrderRequest {
            ModelValidator.validate(this)
            return ServiceOrderRequest(
                    CustomerId.of(customerExternalId!!),
                    AddressId.of(addressExternalId!!),
                    serviceCode!!,
                    title!!,
                    description!!,
                    orderDateTime!!,
                    authentication!!
            )
        }
    }
}
