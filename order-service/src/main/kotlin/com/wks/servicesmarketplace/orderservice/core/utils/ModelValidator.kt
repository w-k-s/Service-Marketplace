package com.wks.servicesmarketplace.orderservice.core.utils

import com.wks.servicesmarketplace.orderservice.core.exceptions.ValidationException
import javax.validation.Validation

object ModelValidator {
    private var validator = Validation.buildDefaultValidatorFactory().validator

    fun <T> validate(instance: T, vararg clazz: Class<*>?): T {
        val violations = validator.validate(instance, *clazz)
        if (violations.isNotEmpty()) {
            val fields = violations.map { it.propertyPath.toString() to listOf(it.message) }.toMap()
            throw ValidationException(fields)
        }
        return instance
    }

}