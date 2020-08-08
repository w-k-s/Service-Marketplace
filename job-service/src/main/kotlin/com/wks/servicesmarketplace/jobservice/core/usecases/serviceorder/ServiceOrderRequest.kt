package com.wks.servicesmarketplace.jobservice.core.usecases.serviceorder

import com.wks.servicesmarketplace.jobservice.core.auth.User
import com.wks.servicesmarketplace.jobservice.core.utils.ModelValidator
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class ServiceOrderRequest constructor(
        val customerExternalId: Long,
        val addressExternalId: Long,
        val serviceCategoryId: Long,
        val title: String,
        val description: String,
        val orderDateTime: String,
        val user: User
) {
    class Builder {
        @field:NotNull
        var customerExternalId: Long? = 0

        @field:NotNull
        var addressExternalId: Long? = 0

        @field:NotNull
        var serviceCategoryId: Long? = null

        @field:NotBlank
        var title: String? = null

        @field:NotBlank
        var description: String? = null

        @field:NotBlank
        var orderDateTime: String? = null

        @field:NotNull
        var user: User? = null

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

        fun user(user: User?): Builder {
            this.user = user
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
                    user!!
            )
        }
    }
}
