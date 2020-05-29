package com.wks.servicesmarketplace.jobservice.core.models.serviceorder.aggregates

import com.wks.servicesmarketplace.jobservice.core.models.Money
import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.ServiceOrderStatus
import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.commands.CreateServiceOrderCommand
import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.commands.VerifyServiceOrderCommand
import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.events.CreateServiceOrderEvent
import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.events.VerifyServiceOrderEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
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
                    command.createdBy!!,
                    1
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
            this.version = it.version
        }
    }

    @CommandHandler
    fun verify(command: VerifyServiceOrderCommand){
        when(status){
            ServiceOrderStatus.VERIFYING -> AggregateLifecycle.apply(VerifyServiceOrderEvent(command.orderId!!, command.modifiedBy!!))
            else -> throw IllegalStateException("ServiceOrder can not be verified when it's state is '${this.status}'")
        }
    }

    @EventSourcingHandler
    fun on(event: VerifyServiceOrderEvent){
        this.status = ServiceOrderStatus.PUBLISHED
        this.lastModifiedDate = ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC)
        this.lastModifiedBy = event.modifiedBy
    }

    // verify - change state, lastModifiedBy, lastModifiedDate, version
    // reject - provide reason, change state, lastModifiedBy, lastModifiedDate, version
    // schedule - change scheduledServiceProviderId,  final bid (embedded), state, lastModifiedBy, lastModifiedDate, version
    // withdraw - change state, lastModifiedBy, lastModifiedDate, version
    // cancel - change state, lastModifiedBy, lastModifiedDate, version
    // noshow - change state, lastModifiedBy, lastModifiedDate, version
    // complete - change state, lastModifiedBy, lastModifiedDate, version
}