package com.wks.servicesmarketplace.jobservice.core.models.serviceorder

import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.commands.CreateServiceOrderCommand
import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.commands.VerifyServiceOrderCommand
import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.events.CreateServiceOrderEvent
import com.wks.servicesmarketplace.jobservice.core.models.Money
import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.events.VerifyServiceOrderEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate
import org.hibernate.validator.constraints.Length
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import java.time.ZoneOffset
import java.time.ZonedDateTime
import javax.persistence.Embedded
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Version
import javax.validation.constraints.NotBlank

@Aggregate
class ServiceOrder() {

    @field:AggregateIdentifier
    lateinit var orderId: String

    var customerId: Long = 0

    var serviceCategoryId: Long = 0

    lateinit var title: String

    lateinit var description: String

    lateinit var orderDateTime: ZonedDateTime

    @field:Enumerated(EnumType.STRING)
    var status: ServiceOrderStatus = ServiceOrderStatus.INVALID

    var scheduledServiceProviderId: Long? = null

    @field:Embedded
    var price: Money? = null

    var rejectReason: @NotBlank @Length(min = 15, max = 150) String? = null

    @field:CreatedDate
    var createdDate: ZonedDateTime = ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC)

    @field:CreatedBy
    lateinit var createdBy: String

    @field:LastModifiedDate
    var lastModifiedDate: ZonedDateTime? = null

    @field:LastModifiedBy
    var lastModifiedBy: String? = null

    @field:Version
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
                    command.orderDateTime!!,
                    command.createdBy!!
            )
        )
    }

    @EventSourcingHandler
    fun on(event: CreateServiceOrderEvent) {
        this.orderId = event.orderId
        this.customerId = event.customerId
        this.serviceCategoryId = event.serviceCategoryId
        this.title = event.title

        event.let {
            this.orderId = it.orderId
            this.customerId = it.customerId
            this.serviceCategoryId = it.serviceCategoryId
            this.title = it.title
            this.description = it.description
            this.orderDateTime = it.orderDateTime.withZoneSameInstant(ZoneOffset.UTC)
            this.status = ServiceOrderStatus.VERIFYING
            this.createdBy = it.createdBy
            this.version = 1
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