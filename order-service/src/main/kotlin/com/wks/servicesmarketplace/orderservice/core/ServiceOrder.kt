package com.wks.servicesmarketplace.orderservice.core

import com.wks.servicesmarketplace.orderservice.core.repositories.MonetaryAmountConverter
import java.time.OffsetDateTime
import java.time.ZoneOffset
import javax.money.MonetaryAmount
import javax.persistence.*

@Entity
@Table(name = "service_order")
data class ServiceOrder internal constructor(
        @EmbeddedId
        @AttributeOverride(name = "value", column = Column(name = "order_uuid"))
        val orderUUID: OrderUUID,
        @AttributeOverride(name = "value", column = Column(name = "customer_uuid"))
        val customerUUID: CustomerUUID,
        val serviceCode: String,
        val title: String,
        val description: String,
        val orderDateTime: OffsetDateTime,
        @PrimaryKeyJoinColumn
        @OneToOne
        val address: Address,
        val status: ServiceOrderStatus = ServiceOrderStatus.INVALID,
        val scheduledCompanyId: CompanyId? = null,
        @field:Convert(converter = MonetaryAmountConverter::class)
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

                ServiceOrder(
                        orderId,
                        customerId,
                        serviceCategoryId,
                        title,
                        description,
                        orderDateTime,
                        address,
                        status,
                        createdBy = createdBy
                )
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