package com.wks.servicesmarketplace.orderservice.core

import com.wks.servicemarketplace.common.*
import java.security.Principal
import java.time.OffsetDateTime
import javax.money.MonetaryAmount
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import javax.validation.constraints.NotBlank
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

data class ServiceOrder internal constructor(
        val id: OrderId,
        val uuid: OrderUUID,
        val customerUUID: CustomerUUID,
        val serviceCode: Service,
        @NotBlank
        val title: String,
        @field:NotBlank
        val description: String,
        @field:ServiceOrderDateTime
        val orderDateTime: OffsetDateTime,
        val address: Address,
        val status: ServiceOrderStatus = ServiceOrderStatus.INVALID,
        val scheduledCompanyId: CompanyId? = null,
        val price: MonetaryAmount? = null,
        val rejectReason: String? = null,
        val createdDate: OffsetDateTime = OffsetDateTime.now(),
        val createdBy: Principal,
        val lastModifiedDate: OffsetDateTime? = null,
        val lastModifiedBy: Principal? = null,
        val version: Long = 0
) {

    companion object {
        fun create(
                orderId: OrderId,
                orderUUID: OrderUUID,
                customerId: CustomerUUID,
                service: Service,
                title: String,
                description: String,
                address: Address,
                orderDateTime: OffsetDateTime,
                status: ServiceOrderStatus,
                createdBy: Principal
        ) =
                ModelValidator.validate(
                        ServiceOrder(
                                orderId,
                                orderUUID,
                                customerId,
                                service,
                                title,
                                description,
                                orderDateTime,
                                address,
                                status,
                                createdBy = createdBy
                        )
                )
    }

    fun verify(verifiedBy: Principal): ServiceOrder {
        return this.copy(
                status = ServiceOrderStatus.PUBLISHED,
                lastModifiedBy = verifiedBy
        )
    }

    fun reject(rejectReason: String, rejectedBy: Principal): ServiceOrder {
        return this.copy(
                rejectReason = rejectReason,
                status = ServiceOrderStatus.REJECTED,
                lastModifiedBy = rejectedBy
        )
    }
}

enum class ServiceOrderStatus {
    INVALID,
    VERIFYING,
    REJECTING,
    REJECTED,
    PUBLISHED,
    BIDS_RECEIVED,
    WITHDRAWING,
    WITHDRAWN,
    AUTHORIZING_PAYMENT,
    PAYMENT_REJECTED,
    SCHEDULED,
    CANCELLING,
    CANCELLED,
    CANCELLING_NO_SHOW,
    NO_SHOW,
    IN_PROGRESS,
    COMPLETED
}