package com.wks.servicemarketplace.common

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import java.util.*
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
import kotlin.reflect.KClass

data class UserId private constructor(@NotNull @JsonValue val value: UUID) {
    companion object {
        @JvmStatic
        fun of(uuid: UUID) = ModelValidator.validate(UserId(uuid))

        @JvmStatic
        fun fromString(uuidString: String) = UserId(UUID.fromString(uuidString))

        @JvmStatic
        fun random() = UserId(UUID.randomUUID())
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

data class Name internal constructor(
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