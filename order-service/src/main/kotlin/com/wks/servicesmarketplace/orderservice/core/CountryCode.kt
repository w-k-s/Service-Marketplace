package com.wks.servicesmarketplace.orderservice.core

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.wks.servicesmarketplace.orderservice.core.exceptions.CountryNotFoundException
import javax.persistence.Embeddable

@Embeddable
class CountryCode internal constructor(@JsonValue var value: String) {
    companion object {
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        @JvmStatic
        fun of(countryCode: String): CountryCode {
            com.neovisionaries.i18n.CountryCode.getByAlpha2Code(countryCode)
                    ?: throw CountryNotFoundException(countryCode, "ISO 3166-1 alpha-2")
            return CountryCode(countryCode)
        }
    }

    override fun toString() = value
}