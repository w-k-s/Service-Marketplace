package com.wks.servicesmarketplace.orderservice.core.usecases.serviceorder

import com.wks.servicesmarketplace.orderservice.core.auth.Authentication
import com.wks.servicesmarketplace.orderservice.core.utils.ModelValidator
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class ServiceOrderRequest constructor(
        val customerExternalId: Long,
        val addressExternalId: Long,
        val serviceCategoryId: Long,
        val title: String,
        val description: String,
        val orderDateTime: String,
        val authentication: Authentication
) {
    class Builder {
        @field:NotNull
        var customerExternalId: Long? = null

        @field:NotNull
        var addressExternalId: Long? = null

        @field:NotNull
        var serviceCategoryId: Long? = null

        @field:NotBlank
        var title: String? = null

        @field:NotBlank
        var description: String? = null

        @field:NotBlank
        var orderDateTime: String? = null

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

        fun serviceCategoryId(serviceCategoryId: Long?): Builder {
            this.serviceCategoryId = serviceCategoryId
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

        fun orderDateTime(orderDateTime: String?): Builder {
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
                    customerExternalId!!,
                    addressExternalId!!,
                    serviceCategoryId!!,
                    title!!,
                    description!!,
                    orderDateTime!!,
                    authentication!!
            )
        }
    }
}
