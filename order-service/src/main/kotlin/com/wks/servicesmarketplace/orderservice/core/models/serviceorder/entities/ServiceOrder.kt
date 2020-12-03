package com.wks.servicesmarketplace.orderservice.core.models.serviceorder.entities

import com.wks.servicesmarketplace.orderservice.core.models.serviceorder.ServiceOrderStatus
import java.time.ZoneOffset
import java.time.ZonedDateTime
import javax.persistence.*

@Entity
@Table(name = "service_order")
data class ServiceOrder private constructor(@field:Id
                                            val orderId: String,
                                            val customerId: Long,
                                            val serviceCategoryId: Long,
                                            val title: String,
                                            val description: String,
                                            val orderDateTime: ZonedDateTime,
                                            val addressExternalId: Long,
                                            @field:Enumerated(EnumType.STRING)
                                            val status: ServiceOrderStatus = ServiceOrderStatus.INVALID,
                                            val scheduledServiceProviderId: Long? = null,
                                            @field:Embedded
                                            val price: Money? = null,
                                            val rejectReason: String? = null,
                                            val createdDate: ZonedDateTime = ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC),
                                            val createdBy: String,
                                            val lastModifiedDate: ZonedDateTime? = null,
                                            val lastModifiedBy: String? = null,
                                            @field:Version
                                            val version: Long = 0) {


    companion object {
        fun create(orderId: String,
                   customerId: Long,
                   serviceCategoryId: Long,
                   title: String,
                   description: String,
                   addressExternalId: Long,
                   orderDateTime: ZonedDateTime,
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
                lastModifiedDate = ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC)
        )
    }

    fun reject(rejectReason: String, rejectedBy: String): ServiceOrder {
        return this.copy(
                status = ServiceOrderStatus.REJECTED,
                lastModifiedBy = rejectedBy,
                lastModifiedDate = ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC)
        )
    }
}