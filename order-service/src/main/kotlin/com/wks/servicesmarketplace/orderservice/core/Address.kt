package com.wks.servicesmarketplace.orderservice.core

import com.wks.servicesmarketplace.orderservice.core.utils.ModelValidator
import org.hibernate.validator.constraints.Length
import java.math.BigDecimal
import javax.persistence.*
import javax.validation.constraints.*

@Embeddable
data class Address private constructor(
        @field:NotBlank
        @field:Length(min = 2, max = 100)
        @Column(name = "address_line1")
        val line1: String,

        @field:Length(min = 2, max = 100)
        @Column(name = "address_line2")
        val line2: String?,

        @field:Length(min = 2, max = 60)
        @Column(name = "address_city")
        val city: String,

        @Embedded
        @AttributeOverride(name = "value", column = Column(name = "address_country"))
        val country: CountryCode,

        @field:DecimalMin("-90")
        @field:DecimalMax("90")
        @Column(name = "address_latitude", precision = 9, scale = 5)
        val latitude: BigDecimal,

        @field:DecimalMin("-180")
        @field:DecimalMax("180")
        @Column(name = "address_longitude", precision = 9, scale = 5)
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