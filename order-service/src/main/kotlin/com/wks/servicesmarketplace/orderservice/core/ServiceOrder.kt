package com.wks.servicesmarketplace.orderservice.core

import com.wks.servicesmarketplace.orderservice.core.repositories.MonetaryAmountConverter
import com.wks.servicesmarketplace.orderservice.core.utils.ModelValidator
import com.wks.servicesmarketplace.orderservice.core.utils.ServiceOrderDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import javax.money.MonetaryAmount
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "service_order")
data class ServiceOrder internal constructor(
        @EmbeddedId
        @AttributeOverride(name = "value", column = Column(name = "order_uuid"))
        val orderUUID: OrderUUID,
        @Embedded
        @AttributeOverride(name = "value", column = Column(name = "customer_uuid"))
        val customerUUID: CustomerUUID,
        @field:NotBlank
        val serviceCode: String,
        @field:NotBlank
        val title: String,
        @field:NotBlank
        val description: String,
        @field:ServiceOrderDateTime
        val orderDateTime: OffsetDateTime,
        @Embedded
        val address: Address,
        val status: ServiceOrderStatus = ServiceOrderStatus.INVALID,
        @AttributeOverride(name = "value", column = Column(name = "scheduled_service_provider_id"))
        val scheduledCompanyId: CompanyId? = null,
        @Convert(converter = MonetaryAmountConverter::class)
        val price: MonetaryAmount? = null,
        val rejectReason: String? = null,
        val createdDate: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC),
        val createdBy: String,
        val lastModifiedDate: OffsetDateTime? = null,
        val lastModifiedBy: String? = null,
        val version: Long = 0) {


    companion object {
        fun create(orderId: OrderUUID,
                   customerId: CustomerUUID,
                   serviceCategoryId: String,
                   title: String,
                   description: String,
                   address: Address,
                   orderDateTime: OffsetDateTime,
                   status: ServiceOrderStatus,
                   createdBy: String) =

                ModelValidator.validate(ServiceOrder(
                        orderId,
                        customerId,
                        serviceCategoryId,
                        title,
                        description,
                        orderDateTime,
                        address,
                        status,
                        createdBy = createdBy
                ))
    }

    fun verify(verifiedBy: String): ServiceOrder {
        return this.copy(
                status = ServiceOrderStatus.PUBLISHED,
                lastModifiedBy = verifiedBy,
                lastModifiedDate = OffsetDateTime.now(ZoneOffset.UTC)
        )
    }

    fun reject(rejectReason: String, rejectedBy: String): ServiceOrder {
        return this.copy(
                status = ServiceOrderStatus.REJECTED,
                lastModifiedBy = rejectedBy,
                lastModifiedDate = OffsetDateTime.now(ZoneOffset.UTC)
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