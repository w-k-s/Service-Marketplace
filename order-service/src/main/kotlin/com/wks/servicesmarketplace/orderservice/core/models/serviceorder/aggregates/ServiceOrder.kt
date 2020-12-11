package com.wks.servicesmarketplace.orderservice.core.models.serviceorder.aggregates

import com.wks.servicesmarketplace.orderservice.core.exceptions.InvalidStateTransitionException
import com.wks.servicesmarketplace.orderservice.core.models.serviceorder.ServiceOrderStatus
import com.wks.servicesmarketplace.orderservice.core.models.serviceorder.commands.CreateServiceOrderCommand
import com.wks.servicesmarketplace.orderservice.core.models.serviceorder.commands.RejectServiceOrderCommand
import com.wks.servicesmarketplace.orderservice.core.models.serviceorder.commands.VerifyServiceOrderCommand
import com.wks.servicesmarketplace.orderservice.core.models.serviceorder.entities.*
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.*
import org.axonframework.spring.stereotype.Aggregate
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime

@Aggregate
class ServiceOrder() {

    @field:AggregateIdentifier
    lateinit var orderId: OrderUUID

    lateinit var customerId: CustomerId

    lateinit var serviceCode: String

    lateinit var title: String

    lateinit var description: String

    lateinit var orderDateTime: OffsetDateTime

    lateinit var address: Address

    var status: ServiceOrderStatus = ServiceOrderStatus.INVALID

    var scheduledCompanyId: CompanyId? = null

    var price: Money? = null

    var rejectReason: String? = null

    var createdDate: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC)

    lateinit var createdBy: String

    var lastModifiedDate: OffsetDateTime? = null

    var lastModifiedBy: String? = null

    @AggregateVersion
    var version: Long = 0

    @CommandHandler
    constructor(command: CreateServiceOrderCommand) : this() {
        AggregateLifecycle.apply(
                CreateServiceOrderEvent(
                        command.orderId!!,
                        command.customerId!!,
                        command.serviceCode!!,
                        command.title!!,
                        command.description!!,
                        command.address.let {
                            CreateServiceOrderEvent.Address(
                                    it!!.externalId!!,
                                    it.name!!,
                                    it.line1!!,
                                    it.line2,
                                    it.city,
                                    it.country,
                                    it.latitude,
                                    it.longitude,
                                    it.version
                            )
                        },
                        command.orderDateTime!!,
                        ServiceOrderStatus.VERIFYING,
                        command.createdBy!!
                )
        )
    }

    @EventSourcingHandler
    fun on(event: CreateServiceOrderEvent) {
        event.let {
            this.orderId = it.orderId
            this.customerId = it.customerId
            this.serviceCode = it.serviceCode
            this.title = it.title
            this.description = it.description
            this.address = it.address.let {
                Address(
                        it.externalId,
                        event.customerId,
                        it.name,
                        it.line1,
                        it.line2,
                        it.city,
                        it.country,
                        it.latitude,
                        it.longitude
                )
            }
            this.orderDateTime = it.orderDateTime
            this.status = it.status
            this.createdBy = it.createdBy
        }
    }

    @CommandHandler
    fun verify(command: VerifyServiceOrderCommand) {
        when (status) {
            ServiceOrderStatus.VERIFYING -> AggregateLifecycle.apply(VerifyServiceOrderEvent(command.orderId!!, command.modifiedBy!!))
            else -> throw InvalidStateTransitionException(ServiceOrder::class, this.status.name, ServiceOrderStatus.PUBLISHED.name)
        }
    }

    @EventSourcingHandler
    fun on(event: VerifyServiceOrderEvent) {
        this.status = ServiceOrderStatus.PUBLISHED
        this.lastModifiedBy = event.modifiedBy
        this.lastModifiedDate = OffsetDateTime.now(ZoneOffset.UTC)
    }

    @CommandHandler
    fun reject(command: RejectServiceOrderCommand) {
        when (status) {
            ServiceOrderStatus.VERIFYING -> AggregateLifecycle.apply(RejectServiceOrderEvent(command.orderId!!, command.rejectReason!!, command.modifiedBy!!))
            else -> throw throw InvalidStateTransitionException(ServiceOrder::class, this.status.name, ServiceOrderStatus.REJECTED.name)
        }
    }

    @EventSourcingHandler
    fun on(event: RejectServiceOrderEvent) {
        this.status = ServiceOrderStatus.REJECTED
        this.rejectReason = event.rejectReason
        this.lastModifiedBy = event.modifiedBy
        this.lastModifiedDate = OffsetDateTime.now(ZoneOffset.UTC)
    }

    // schedule - change scheduledServiceProviderId,  final bid (embedded), state, lastModifiedBy, lastModifiedDate, version
    // withdraw - change state, lastModifiedBy, lastModifiedDate, version
    // cancel - change state, lastModifiedBy, lastModifiedDate, version
    // noshow - change state, lastModifiedBy, lastModifiedDate, version
    // complete - change state, lastModifiedBy, lastModifiedDate, version
}

data class CreateServiceOrderEvent(val orderId: OrderUUID,
                                   val customerId: CustomerId,
                                   val serviceCode: String,
                                   val title: String,
                                   val description: String,
                                   val address: Address,
                                   val orderDateTime: OffsetDateTime,
                                   val status: ServiceOrderStatus,
                                   val createdBy: String) {
    data class Address(
            val externalId: AddressId,
            val name: String,
            val line1: String,
            val line2: String?,
            val city: String,
            val country: String,
            val latitude: BigDecimal,
            val longitude: BigDecimal,
            val version: Long
    )
}

data class RejectServiceOrderEvent(
        val orderId: OrderUUID,
        val rejectReason: String,
        val modifiedBy: String
)

data class VerifyServiceOrderEvent(
        val orderId: OrderUUID,
        val modifiedBy: String
)