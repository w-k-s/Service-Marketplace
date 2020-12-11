package com.wks.servicesmarketplace.orderservice.core.models.serviceorder.commands.constraints.datetime

import java.time.OffsetDateTime
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class ServiceOrderDateTimeValidator : ConstraintValidator<ServiceOrderDateTime, OffsetDateTime> {
    override fun isValid(value: OffsetDateTime?, context: ConstraintValidatorContext?): Boolean {
        if (value == null || value.isBefore(OffsetDateTime.now())) {
            return false
        }
        return true
    }
}