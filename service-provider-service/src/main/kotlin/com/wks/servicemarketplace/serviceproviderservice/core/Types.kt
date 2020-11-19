package com.wks.servicemarketplace.serviceproviderservice.core

import com.fasterxml.jackson.annotation.JsonValue
import com.wks.servicemarketplace.serviceproviderservice.core.utils.ModelValidator
import javax.validation.constraints.NotBlank

open class Id<T>(
        @JsonValue open val value: T
)

data class Email internal constructor(
        @NotBlank
        @javax.validation.constraints.Email
        @JsonValue val value: String
) {
    companion object {
        fun of(value: String) = ModelValidator.validate(Email(value))
    }

    override fun toString() = value
}

data class PhoneNumber(
        @NotBlank
        @JsonValue val value: String
) {
    companion object {
        fun of(value: String) = PhoneNumber(value)
    }

    override fun toString() = value
}