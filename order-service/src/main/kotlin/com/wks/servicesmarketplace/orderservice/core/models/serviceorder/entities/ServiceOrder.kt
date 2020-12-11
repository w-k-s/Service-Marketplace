package com.wks.servicesmarketplace.orderservice.core.models.serviceorder.entities

import com.wks.servicesmarketplace.orderservice.core.models.serviceorder.ServiceOrderStatus
import java.time.OffsetDateTime
import java.time.ZoneOffset
import javax.persistence.*

@Entity
@Table(name = "service_order")
data class ServiceOrder internal constructor(@field:EmbeddedId
                                            @AttributeOverride(name = "value", column = Column(name = "order_id"))
                                            val orderId: OrderUUID,
                                            @AttributeOverride(name = "value", column = Column(name = "customer_id"))
                                            val customerId: CustomerId,
                                            val serviceCode: String,
                                            val title: String,
                                            val description: String,
                                            val orderDateTime: OffsetDateTime,
                                            @AttributeOverride(name = "value", column = Column(name = "address_external_id"))
                                            val addressExternalId: AddressId,
                                            @field:Enumerated(EnumType.STRING)
                                            val status: ServiceOrderStatus = ServiceOrderStatus.INVALID,
                                            @AttributeOverride(name = "value", column = Column(name = "company_id"))
                                            val scheduledCompanyId: CompanyId? = null,
                                            @field:Embedded
                                            val price: Money? = null,
                                            val rejectReason: String? = null,
                                            val createdDate: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC),
                                            val createdBy: String,
                                            val lastModifiedDate: OffsetDateTime? = null,
                                            val lastModifiedBy: String? = null,
                                            @field:Version
                                            val version: Long = 0) {


    companion object {
        fun create(orderId: OrderUUID,
                   customerId: CustomerId,
                   serviceCategoryId: String,
                   title: String,
                   description: String,
                   addressExternalId: AddressId,
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
                        addressExternalId,
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