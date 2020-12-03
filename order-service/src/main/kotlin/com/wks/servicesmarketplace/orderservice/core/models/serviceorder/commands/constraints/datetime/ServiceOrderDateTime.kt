package com.wks.servicesmarketplace.orderservice.core.models.serviceorder.commands.constraints.datetime

import javax.validation.Constraint
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