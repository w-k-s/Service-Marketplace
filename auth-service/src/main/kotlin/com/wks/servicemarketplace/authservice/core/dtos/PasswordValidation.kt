package com.wks.servicemarketplace.authservice.core.dtos

import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass


@Target(AnnotationTarget.FIELD)
@MustBeDocumented
@Constraint(validatedBy = [PasswordValidator::class])
annotation class Password(
        val message: String = "Password must be at least 16 characters long with at least one uppercase, lowercase and a number",
        val groups: Array<KClass<*>> = [],
        val payload: Array<KClass<out Payload>> = []
)

class PasswordValidator : ConstraintValidator<Password, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) return false
        return value.length >= 16
                && value.contains(Regex("[A-Z]"))
                && value.contains(Regex("[a-z]"))
                && value.contains(Regex("[1-9]"))
                && !value.contains(' ')
    }
}