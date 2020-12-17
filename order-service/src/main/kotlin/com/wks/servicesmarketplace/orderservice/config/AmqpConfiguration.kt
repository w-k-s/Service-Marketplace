package com.wks.servicesmarketplace.orderservice.config

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class AmqpConfiguration {

    companion object {
        const val EXCHANGE_NAME = "com.wks.servicemarketplace.customer.exchange"
    }

    @Bean
    fun rabbitAdmin(rabbitTemplate: RabbitTemplate): RabbitAdmin {
        return RabbitAdmin(rabbitTemplate)
    }

    @Bean
    fun topicExchange() = TopicExchange(EXCHANGE_NAME, true, true)

}