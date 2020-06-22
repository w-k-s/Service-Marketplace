package com.wks.servicesmarketplace.jobservice.config

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AmqpConfiguration {

    companion object {
        val EXCHANGE_NAME = "com.wks.servicemarketplace.account.exchange"
    }

    @Bean
    fun topicExchange() = TopicExchange(EXCHANGE_NAME, true, true)

    @Bean
    fun addressAddedQueue() = Queue(QueueName.CUSTOMER_ADDRESS_ADDED)

    @Bean
    fun addressAddedBinding(topicExchange: TopicExchange, addressAddedQueue: Queue) : Binding = BindingBuilder.bind(addressAddedQueue)
            .to(topicExchange)
            .with("com.wks.servicemarketplace.account.customer.address.added")
}