package com.wks.servicesmarketplace.orderservice.core.usecases

import com.wks.servicesmarketplace.orderservice.core.CustomerUUID
import com.wks.servicesmarketplace.orderservice.core.auth.Authentication
import com.wks.servicesmarketplace.orderservice.core.utils.ServiceOrderDateTime
import com.wks.servicesmarketplace.orderservice.core.utils.ModelValidator
import java.math.BigDecimal
import java.time.OffsetDateTime
import javax.validation.constraints.*

data class ServiceOrderRequest constructor(
        val customerUUID: CustomerUUID,
        val address: Address,
        val serviceCode: String,
        val title: String,
        val description: String,
        val orderDateTime: OffsetDateTime,
        val authentication: Authentication
) {
    data class Address(
            val line1: String,
            val line2: String? = null,
            val city: String,
            val country: String,
            val latitude: BigDecimal,
            val longitude: BigDecimal
    ) {
        class Builder {
            @field:NotNull
            @field:NotBlank
            @field:Size(min = 2, max = 100)
            val line1: String? = null

            @field:Size(min = 2, max = 100)
            val line2: String? = null

            @field:Size(min = 2, max = 60)
            val city: String? = null

            @field:NotBlank
            @field:NotNull
            @field:Size(min = 2, max = 2)
            val country: String? = null

            @field:NotNull
            @field:DecimalMin("-90")
            @field:DecimalMax("90")
            val latitude: BigDecimal? = null

            @field:NotNull
            @field:DecimalMin("-180")
            @field:DecimalMax("180")
            val longitude: BigDecimal? = null
        }
    }

    class Builder {
        @field:NotNull
        var customerUUID: String? = null

        @field:NotNull
        var address: Address.Builder? = null

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

        fun authentication(authentication: Authentication?): Builder {
            this.authentication = authentication
            return this
        }

        fun build(): ServiceOrderRequest {
            ModelValidator.validate(this)
            return ServiceOrderRequest(
                    CustomerUUID.fromString(customerUUID!!),
                    address!!.let {
                        Address(
                                it.line1!!,
                                it.line2!!,
                                it.city!!,
                                it.country!!,
                                it.latitude!!,
                                it.longitude!!
                        )
                    },
                    serviceCode!!,
                    title!!,
                    description!!,
                    orderDateTime!!,
                    authentication!!
            )
        }
    }
}
