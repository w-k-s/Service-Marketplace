package com.wks.servicemarketplace.common

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import com.wks.servicemarketplace.common.auth.UserType
import com.wks.servicemarketplace.common.errors.CoreException
import com.wks.servicemarketplace.common.errors.ErrorType
import com.wks.servicemarketplace.common.ids.UserId
import java.lang.UnsupportedOperationException
import java.util.*
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size
import kotlin.reflect.KClass

data class CustomerId private constructor(@JsonValue val value: UserId) {
    companion object {
        @JvmStatic
        fun of(userId: UserId) = CustomerId(userId)

        @JvmStatic
        fun fromString(idString: String) = CustomerId(UserId.fromString(idString))
    }

    init {
        if (value.userType != UserType.CUSTOMER){
            throw CoreException(ErrorType.VALIDATION, "Invalid UserId. User is not a customer")
        }
    }

    override fun toString() = value.toString()
}

data class CompanyRepresentativeId(@JsonValue val value: UserId) {
    companion object {
        @Deprecated(message="Use UserId")
        fun random() : CompanyRepresentativeId = throw UnsupportedOperationException("Can not generate random CompanyRepresentativeId")
        fun fromString(userIdString: String) = CompanyRepresentativeId(UserId.fromString(userIdString))
        fun of(userId: UserId) = CompanyRepresentativeId(userId)
    }

    init {
        if (value.userType != UserType.SERVICE_PROVIDER){
            throw CoreException(ErrorType.VALIDATION, "Invalid UserId. User is not a service provider")
        }
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

data class CompanyId(@JsonValue val value: Long)
data class CompanyUUID(@JsonValue val value: UUID) {
    companion object {
        fun random() = CompanyUUID(UUID.randomUUID())
        fun fromString(uuidString: String) = CompanyUUID(UUID.fromString(uuidString))
    }
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
                ?: throw CoreException(ErrorType.VALIDATION, "Invalid country: $code"))
    }

    override fun toString() = code
}

enum class Service(val code: String, val numericCode: Int) {
    HOUSE_KEEPING("CLEAN",100),
    ELECTRICAL("ELCTRC",200);

    override fun toString() = code

    companion object {
        fun of(code: String) = values().first { it.code == code }
    }
}

class Services(services: List<Service>) : Iterable<Service> {
    private val services = EnumSet.copyOf(services)

    companion object {
        fun of(codes: List<String>) = Services(codes.map { Service.of(it) })
    }

    constructor(vararg services: Service) : this(services.asList())

    override fun iterator() = services.iterator()

    override fun equals(other: Any?): Boolean {
        return (other as? Services)?.services == services
    }

    override fun hashCode(): Int {
        return services?.hashCode() ?: 0
    }

    override fun toString() = this.services.map { it.code }.joinToString { ", " }
}