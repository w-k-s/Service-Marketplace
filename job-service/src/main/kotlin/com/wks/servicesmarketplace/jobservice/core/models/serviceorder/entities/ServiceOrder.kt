package com.wks.servicesmarketplace.jobservice.core.models.serviceorder.entities

import com.wks.servicesmarketplace.jobservice.core.models.Money
import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.ServiceOrderStatus
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

                                            val version: Long = 0) {


    companion object {
        fun create(orderId: String,
                   customerId: Long,
                   serviceCategoryId: Long,
                   title: String,
                   description: String,
                   orderDateTime: ZonedDateTime,
                   status: ServiceOrderStatus,
                   createdBy: String,
                   version: Long) =

                ServiceOrder(
                        orderId,
                        customerId,
                        serviceCategoryId,
                        title,
                        description,
                        orderDateTime,
                        status,
                        createdBy = createdBy,
                        version = version
                )
    }

}