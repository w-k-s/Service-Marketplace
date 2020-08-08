package com.wks.servicemarketplace.authservice.adapters.graphql.validation

import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass


@Target(AnnotationTarget.FIELD)
@MustBeDocumented
@Constraint(validatedBy = [PasswordValidator::class])
annotation class Password(
        val message: String = "Password must be at least 18 characters long with at least one uppercase, lowercase and a number",
        val groups: Array<KClass<*>> = [],
        val payload: Array<KClass<out Payload>> = []
)

class PasswordValidator : ConstraintValidator<Password, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) return false
        return value.length >= 18
                && value.matches(Regex("^.*[A-Z]+.*[a-z]+.*[0-9]+.*\$"))
                && !value.contains(' ')
    }
}