package com.wks.servicesmarketplace.orderservice.core

import com.wks.servicemarketplace.common.CustomerUUID
import com.wks.servicemarketplace.common.ModelValidator
import com.wks.servicesmarketplace.orderservice.core.repositories.CustomerUUIDConverter
import com.wks.servicesmarketplace.orderservice.core.repositories.MonetaryAmountConverter
import com.wks.servicesmarketplace.orderservice.core.repositories.PrincipalConverter
import com.wks.servicesmarketplace.orderservice.core.utils.ServiceOrderDateTime
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.security.Principal
import java.time.OffsetDateTime
import javax.money.MonetaryAmount
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "service_order")
data class ServiceOrder internal constructor(
    @EmbeddedId
    @AttributeOverride(name = "value", column = Column(name = "order_uuid"))
    val orderUUID: OrderUUID,
    @Convert(converter=CustomerUUIDConverter::class)
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
    @CreatedDate
    val createdDate: OffsetDateTime = OffsetDateTime.now(),
    @Convert(converter = PrincipalConverter::class)
    val createdBy: Principal,
    @LastModifiedDate
    val lastModifiedDate: OffsetDateTime? = null,
    @Convert(converter = PrincipalConverter::class)
    val lastModifiedBy: Principal? = null,
    val version: Long = 0
) {


    companion object {
        fun create(
            orderId: OrderUUID,
            customerId: CustomerUUID,
            serviceCategoryId: String,
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
                    serviceCategoryId,
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