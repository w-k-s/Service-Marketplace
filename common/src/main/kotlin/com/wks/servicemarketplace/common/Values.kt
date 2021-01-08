package com.wks.servicemarketplace.common

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import com.wks.servicemarketplace.common.errors.InvalidCountryException
import java.util.*
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
import kotlin.reflect.KClass

data class UserId private constructor(@JsonValue val value: UUID) {
    companion object {
        @JvmStatic
        fun of(uuid: UUID) = UserId(uuid)

        @JvmStatic
        fun fromString(uuidString: String) = UserId(UUID.fromString(uuidString))

        @JvmStatic
        fun random() = UserId(UUID.randomUUID())
    }

    override fun toString() = value.toString()
}

data class CustomerUUID private constructor(@JsonValue val value: UUID) {
    companion object {
        @JvmStatic
        fun of(userId: UserId) = CustomerUUID(userId.value)

        @JvmStatic
        fun fromString(uuidString: String) = CustomerUUID(UUID.fromString(uuidString))
    }

    override fun toString() = value.toString()
}

data class CustomerId private constructor(@JsonValue val value: Long) {
    companion object {
        @JvmStatic
        fun of(id: Long) = CustomerId(id)
    }

    override fun toString() = value.toString()
}

data class AddressUUID private constructor(@JsonValue val value: UUID) {
    companion object {
        @JvmStatic
        fun of(uuid: UUID) = AddressUUID(uuid)

        @JvmStatic
        fun fromString(uuidString: String) = AddressUUID(UUID.fromString(uuidString))

        @JvmStatic
        fun random() = AddressUUID(UUID.randomUUID())
    }

    override fun toString() = value.toString()
}

data class AddressId private constructor(@JsonValue val value: Long) {
    companion object {
        @JvmStatic
        fun of(id: Long) = AddressId(id)
    }

    override fun toString() = value.toString()
}


@Target(AnnotationTarget.FIELD)
@MustBeDocumented
@Constraint(validatedBy = [PasswordValidator::class])
internal annotation class ValidPassword(
        val message: String = "Password must be at least 16 characters long with at least one uppercase, lowercase and a number",
        val groups: Array<KClass<*>> = [],
        val payload: Array<KClass<out Payload>> = []
)

internal class PasswordValidator : ConstraintValidator<ValidPassword, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) return false
        return value.length >= 16
                && value.contains(Regex("[A-Z]"))
                && value.contains(Regex("[a-z]"))
                && value.contains(Regex("[1-9]"))
                && !value.contains(' ')
    }
}

data class Password internal constructor(@ValidPassword @JsonValue val value: String) {
    companion object {
        @JvmStatic
        fun of(password: String) = ModelValidator.validate(Password(password))
    }
}

data class Email internal constructor(@NotBlank
                                      @javax.validation.constraints.Email
                                      @JsonValue val value: String) {
    companion object {
        @JvmStatic
        fun of(email: String) = ModelValidator.validate(Email(email))
    }

    override fun toString() = value
}

data class PhoneNumber internal constructor(@JsonValue
                                            @NotBlank
                                            val value: String) {
    companion object {
        @JvmStatic
        fun of(phone: String) = ModelValidator.validate(PhoneNumber(phone))
    }

    override fun toString() = value
}

data class Name @JsonCreator internal constructor(
        @JsonProperty("firstName")
        @field:Size(min = 2, max = 50)
        val firstName: String,
        @JsonProperty("lastName")
        @field:Size(min = 2, max = 50)
        val lastName: String) {

    companion object {
        @JvmStatic
        fun of(firstName: String, lastName: String) = ModelValidator.validate(Name(firstName, lastName))
    }

    override fun toString() = "$firstName $lastName"
}

data class CountryCode private constructor(@JsonValue private val code: String) {

    companion object {
        @JvmStatic
        fun of(code: String) = CountryCode(com.neovisionaries.i18n.CountryCode.getByAlpha2Code(code)?.alpha2
                ?: throw InvalidCountryException(code, "ISO 3166-1 alpha-2"))
    }

    override fun toString() = code
}