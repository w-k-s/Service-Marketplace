package com.wks.servicesmarketplace.jobservice.config

import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.ServiceOrder
import org.axonframework.eventsourcing.EventSourcingRepository
import org.axonframework.eventsourcing.eventstore.EventStore
import org.axonframework.modelling.command.Repository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class EventSourcingRepositories {


    @Bean
    fun serviceOrderRepository(eventStore: EventStore): Repository<ServiceOrder> {
        return EventSourcingRepository.builder(ServiceOrder::class.java).eventStore(eventStore).build()
    }
}