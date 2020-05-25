package com.wks.servicesmarketplace.jobservice.core.models.serviceorder.commands.constraints.datetime

import java.time.ZonedDateTime
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class ServiceOrderDateTimeValidator : ConstraintValidator<ServiceOrderDateTime, ZonedDateTime> {
    override fun isValid(value: ZonedDateTime?, context: ConstraintValidatorContext?): Boolean {
        if (value == null || value.isBefore(ZonedDateTime.now())) {
            return false
        }
        return true
    }
}