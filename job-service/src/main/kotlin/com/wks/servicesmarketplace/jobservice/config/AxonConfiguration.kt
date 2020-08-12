package com.wks.servicesmarketplace.jobservice.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.aggregates.ServiceOrder
import org.axonframework.commandhandling.CommandBus
import org.axonframework.eventsourcing.EventSourcingRepository
import org.axonframework.eventsourcing.eventstore.EventStore
import org.axonframework.messaging.interceptors.BeanValidationInterceptor
import org.axonframework.modelling.command.Repository
import org.axonframework.serialization.Serializer
import org.axonframework.serialization.json.JacksonSerializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class AxonConfiguration {

    @Autowired
    fun configureCommandBus(commandBus: CommandBus) {
        commandBus.registerDispatchInterceptor(BeanValidationInterceptor())
    }

    @Bean
    fun serviceOrderEventSourcingRepository(eventStore: EventStore): Repository<ServiceOrder> {
        return EventSourcingRepository.builder(ServiceOrder::class.java).eventStore(eventStore).build()
    }

    @Bean
    fun eventSerializer(objectMapper: ObjectMapper) : Serializer
            = JacksonSerializer.builder().objectMapper(objectMapper).build()
}