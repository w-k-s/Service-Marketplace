package com.wks.servicesmarketplace.orderservice.core.utils

import java.time.OffsetDateTime
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass


@MustBeDocumented
@Constraint(validatedBy = [ServiceOrderDateTimeValidator::class])
@Target(allowedTargets = [AnnotationTarget.FIELD])
@Retention(AnnotationRetention.RUNTIME)
annotation class ServiceOrderDateTime(
        val message: String = "{ServiceOrderDateTime.invalid}",
        val groups: Array<KClass<out Any>> = [],
        val payload: Array<KClass<out Payload>> = []
)

class ServiceOrderDateTimeValidator : ConstraintValidator<ServiceOrderDateTime, OffsetDateTime> {
    override fun isValid(value: OffsetDateTime?, context: ConstraintValidatorContext?): Boolean {
        if (value == null || value.isBefore(OffsetDateTime.now())) {
            return false
        }
        return true
    }
}