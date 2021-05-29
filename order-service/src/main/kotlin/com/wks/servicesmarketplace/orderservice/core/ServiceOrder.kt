package com.wks.servicesmarketplace.orderservice.core

import com.wks.servicemarketplace.common.CustomerUUID
import com.wks.servicemarketplace.common.ModelValidator
import com.wks.servicemarketplace.common.Service
import com.wks.servicesmarketplace.orderservice.core.repositories.CustomerUUIDConverter
import com.wks.servicesmarketplace.orderservice.core.repositories.MonetaryAmountConverter
import com.wks.servicesmarketplace.orderservice.core.repositories.PrincipalConverter
import com.wks.servicesmarketplace.orderservice.core.utils.ServiceCode
import com.wks.servicesmarketplace.orderservice.core.utils.ServiceOrderDateTime
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.security.Principal
import java.time.OffsetDateTime
import javax.money.MonetaryAmount
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class ServiceOrder internal constructor(
    @field:EmbeddedId
    @field:AttributeOverride(name = "value", column = Column(name = "order_uuid"))
    val orderUUID: OrderUUID,
    @field:Convert(converter=CustomerUUIDConverter::class)
    val customerUUID: CustomerUUID,
    @field:NotNull
    @field:ServiceCode
    val serviceCode: Service,
    @field:NotBlank
    val title: String,
    @field:NotBlank
    val description: String,
    @field:ServiceOrderDateTime
    val orderDateTime: OffsetDateTime,
    @field:Embedded
    val address: Address,
    val status: ServiceOrderStatus = ServiceOrderStatus.INVALID,
    @field:AttributeOverride(name = "value", column = Column(name = "scheduled_service_provider_id"))
    val scheduledCompanyId: CompanyId? = null,
    @field:Convert(converter = MonetaryAmountConverter::class)
    val price: MonetaryAmount? = null,
    val rejectReason: String? = null,
    @field:CreatedDate
    val createdDate: OffsetDateTime = OffsetDateTime.now(),
    @field:Convert(converter = PrincipalConverter::class)
    val createdBy: Principal,
    @field:LastModifiedDate
    val lastModifiedDate: OffsetDateTime? = null,
    @field:Convert(converter = PrincipalConverter::class)
    val lastModifiedBy: Principal? = null,
    val version: Long = 0
) {

    companion object {
        fun create(
                orderId: OrderUUID,
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