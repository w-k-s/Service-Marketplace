package com.wks.servicesmarketplace.orderservice.core.utils

import com.wks.servicemarketplace.common.Service
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [ServiceCodeValidator::class])
@Target(allowedTargets = [AnnotationTarget.FIELD])
@Retention(AnnotationRetention.RUNTIME)
annotation class ServiceCode(
        val message: String = "{ServiceCode.invalid}",
        val groups: Array<KClass<out Any>> = [],
        val payload: Array<KClass<out Payload>> = []
)

class ServiceCodeValidator : ConstraintValidator<ServiceCode, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        return Service.values().any { it.code == value }
    }
}