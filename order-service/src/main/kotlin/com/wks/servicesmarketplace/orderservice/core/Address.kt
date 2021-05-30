package com.wks.servicesmarketplace.orderservice.core

import com.wks.servicemarketplace.common.CountryCode
import com.wks.servicemarketplace.common.ModelValidator
import org.hibernate.validator.constraints.Length
import java.math.BigDecimal
import javax.validation.constraints.*

data class Address internal constructor(
    @field:NotBlank
    @field:Length(min = 2, max = 100)
    val line1: String,

    @field:Length(min = 2, max = 100)
    val line2: String?,

    @field:Length(min = 2, max = 60)
    val city: String,

    val country: CountryCode,

    @field:DecimalMin("-90")
    @field:DecimalMax("90")
    val latitude: BigDecimal,

    @field:DecimalMin("-180")
    @field:DecimalMax("180")
    val longitude: BigDecimal
) {
    companion object {
        fun create(
            line1: String,
            line2: String? = null,
            city: String,
            country: CountryCode,
            latitude: BigDecimal,
            longitude: BigDecimal
        ) = ModelValidator.validate(Address(line1, line2, city, country, latitude, longitude))
    }
}