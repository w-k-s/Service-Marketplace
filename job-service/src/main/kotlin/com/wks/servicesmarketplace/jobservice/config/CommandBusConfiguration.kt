package com.wks.servicesmarketplace.jobservice.config

import org.axonframework.commandhandling.CommandBus
import org.axonframework.messaging.interceptors.BeanValidationInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration


@Configuration
class CommandBusConfiguration {

    @Autowired
    fun configureCommandBus(commandBus: CommandBus) {
//        val commandBus: CommandBus = SimpleCommandBus.builder().build()
//        commandBus.registerDispatchInterceptor(BeanValidationInterceptor())
//        commandBus.registerHandlerInterceptor(TransactionManagingInterceptor())
//        return commandBus
        commandBus.registerDispatchInterceptor(BeanValidationInterceptor())
    }
}