package com.wks.servicemarketplace.common

import com.wks.servicemarketplace.common.errors.ValidationException
import javax.validation.Validation

object ModelValidator {
    private var validator = Validation.buildDefaultValidatorFactory().validator

    @JvmStatic
    fun <T> validate(instance: T, vararg clazz: Class<*>?): T {
        val violations = validator.validate(instance, *clazz)
        if (violations.isNotEmpty()) {
            val fields = violations.map { it.propertyPath.toString() to listOf(it.message) }.toMap()
            throw ValidationException(fields)
        }
        return instance
    }
}