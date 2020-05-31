package com.wks.servicesmarketplace.jobservice.core.models.serviceorder.aggregates

import com.wks.servicesmarketplace.jobservice.core.exceptions.InvalidStateTransitionException
import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.ServiceOrderStatus
import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.commands.CreateServiceOrderCommand
import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.commands.RejectServiceOrderCommand
import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.commands.VerifyServiceOrderCommand
import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.entities.Money
import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.events.CreateServiceOrderEvent
import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.events.RejectServiceOrderEvent
import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.events.VerifyServiceOrderEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.AggregateVersion
import org.axonframework.spring.stereotype.Aggregate
import java.time.ZoneOffset
import java.time.ZonedDateTime

@Aggregate
class ServiceOrder() {

    @field:AggregateIdentifier
    lateinit var orderId: String

    var customerId: Long = 0

    var serviceCategoryId: Long = 0

    lateinit var title: String

    lateinit var description: String

    lateinit var orderDateTime: ZonedDateTime

    var status: ServiceOrderStatus = ServiceOrderStatus.INVALID

    var scheduledServiceProviderId: Long? = null

    var price: Money? = null

    var rejectReason: String? = null

    var createdDate: ZonedDateTime = ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC)

    lateinit var createdBy: String

    var lastModifiedDate: ZonedDateTime? = null

    var lastModifiedBy: String? = null

    @AggregateVersion
    var version: Long = 0

    @CommandHandler
    constructor(command: CreateServiceOrderCommand) : this(){
        AggregateLifecycle.apply(
            CreateServiceOrderEvent(
                    command.orderId!!,
                    command.customerId,
                    command.serviceCategoryId,
                    command.title!!,
                    command.description!!,
                    command.orderDateTime!!.withZoneSameInstant(ZoneOffset.UTC),
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
            this.serviceCategoryId = it.serviceCategoryId
            this.title = it.title
            this.description = it.description
            this.orderDateTime = it.orderDateTime
            this.status = it.status
            this.createdBy = it.createdBy
        }
    }

    @CommandHandler
    fun verify(command: VerifyServiceOrderCommand){
        when(status){
            ServiceOrderStatus.VERIFYING -> AggregateLifecycle.apply(VerifyServiceOrderEvent(command.orderId!!, command.modifiedBy!!))
            else -> throw InvalidStateTransitionException(ServiceOrder::class, this.status.name, ServiceOrderStatus.PUBLISHED.name)
        }
    }

    @EventSourcingHandler
    fun on(event: VerifyServiceOrderEvent){
        this.status = ServiceOrderStatus.PUBLISHED
        this.lastModifiedBy = event.modifiedBy
        this.lastModifiedDate = ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC)
    }

    @CommandHandler
    fun reject(command: RejectServiceOrderCommand){
        when(status){
            ServiceOrderStatus.VERIFYING -> AggregateLifecycle.apply(RejectServiceOrderEvent(command.orderId!!, command.rejectReason!!, command.modifiedBy!!))
            else -> throw throw InvalidStateTransitionException(ServiceOrder::class, this.status.name, ServiceOrderStatus.REJECTED.name)
        }
    }

    @EventSourcingHandler
    fun on(event: RejectServiceOrderEvent){
        this.status = ServiceOrderStatus.REJECTED
        this.rejectReason = event.rejectReason
        this.lastModifiedBy = event.modifiedBy
        this.lastModifiedDate = ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC)
    }

    // schedule - change scheduledServiceProviderId,  final bid (embedded), state, lastModifiedBy, lastModifiedDate, version
    // withdraw - change state, lastModifiedBy, lastModifiedDate, version
    // cancel - change state, lastModifiedBy, lastModifiedDate, version
    // noshow - change state, lastModifiedBy, lastModifiedDate, version
    // complete - change state, lastModifiedBy, lastModifiedDate, version
}