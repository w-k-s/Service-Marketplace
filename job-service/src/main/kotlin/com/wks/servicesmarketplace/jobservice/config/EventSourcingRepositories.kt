package com.wks.servicesmarketplace.jobservice.config

import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.aggregates.ServiceOrder
import org.axonframework.common.jpa.EntityManagerProvider
import org.axonframework.eventsourcing.EventSourcingRepository
import org.axonframework.eventsourcing.eventstore.EventStore
import org.axonframework.modelling.command.Repository
import org.axonframework.springboot.util.jpa.ContainerManagedEntityManagerProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class EventSourcingRepositories {

    @Bean
    fun serviceOrderEventSourcingRepository(eventStore: EventStore): Repository<ServiceOrder> {
        return EventSourcingRepository.builder(ServiceOrder::class.java).eventStore(eventStore).build()
    }

    /**
     * EmbeddedEventStore is auto-configured by;
     * 1. excluding the axon-server-connector dependency from axon-spring-boot-starter
     * 2. JPA being present in the classpath (this auto-configures tha JpaEventStorageEngine)
     * 3. Making the commandEntityManager the @primary entity manager bean
     * 4. Adding this bean which leverages Spring to inject the @primary entityManager bean.
     */
    @Bean
    fun entityManagerProvider(): EntityManagerProvider? {
        return ContainerManagedEntityManagerProvider()
    }

}