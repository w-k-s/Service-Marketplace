package com.wks.servicemarketplace.serviceproviderservice.core

import com.fasterxml.jackson.annotation.JsonValue
import com.wks.servicemarketplace.serviceproviderservice.core.exceptions.CountryCodeNotFoundException;

class CountryCode(code: String) {
    @JsonValue
    private val countryCode: String = com.neovisionaries.i18n.CountryCode.getByAlpha2Code(code)?.alpha2
            ?: throw CountryCodeNotFoundException(code, "ISO 3166-1 alpha-2")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return (other as CountryCode).countryCode == countryCode
    }

    override fun hashCode() = countryCode.hashCode()
    override fun toString() = countryCode
}