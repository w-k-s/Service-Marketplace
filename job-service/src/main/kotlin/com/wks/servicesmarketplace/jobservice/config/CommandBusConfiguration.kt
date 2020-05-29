package com.wks.servicesmarketplace.jobservice.config

import org.axonframework.commandhandling.CommandBus
import org.axonframework.messaging.interceptors.BeanValidationInterceptor
import org.axonframework.messaging.interceptors.TransactionManagingInterceptor
import org.axonframework.spring.messaging.unitofwork.SpringTransactionManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager


@Configuration
class CommandBusConfiguration {

    @Autowired
    fun configureCommandBus(commandBus: CommandBus,
                            @Qualifier("commandTransactionManager") commandTransactionManager : PlatformTransactionManager) {
        commandBus.registerDispatchInterceptor(BeanValidationInterceptor())
        commandBus.registerHandlerInterceptor(TransactionManagingInterceptor(SpringTransactionManager(commandTransactionManager)))
    }
}