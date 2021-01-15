package com.wks.servicesmarketplace.orderservice.core.usecases

import com.wks.servicemarketplace.common.ModelValidator
import com.wks.servicemarketplace.common.auth.Authentication
import java.math.BigDecimal
import java.time.OffsetDateTime
import javax.validation.constraints.*

data class ServiceOrderRequest constructor(
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
            @NotBlank
            val line1: String? = null

            val line2: String? = null

            @NotBlank
            val city: String? = null

            @NotBlank
            val country: String? = null

            @NotNull
            val latitude: BigDecimal? = null

            @NotNull
            val longitude: BigDecimal? = null
        }
    }

    class Builder {


        @field:NotNull
        var address: Address.Builder? = null

        @field:NotBlank
        var serviceCode: String? = null

        @field:NotBlank
        var title: String? = null

        @field:NotBlank
        var description: String? = null

        @field:NotNull
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
