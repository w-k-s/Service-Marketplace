package com.wks.servicemarketplace.authservice.api

import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.Channel

class AuthMessaging {

    enum class Exchange(val exchangeName: String) {
        MAIN("com.wks.servicemarketplace.auth.exchange.main");

        fun declare(channel: Channel) {
            channel.exchangeDeclare(this.exchangeName, BuiltinExchangeType.TOPIC, true, false, false, emptyMap());
        }
    }

    enum class Queue(val queueName: String){
        CUSTOMER_PROFILE_CREATED("com.wks.servicemarketplace.auth.queue.customerCreated"),
        CUSTOMER_PROFILE_CREATION_FAILED("com.wks.servicemarketplace.auth.queue.customerCreationFailed"),
        SERVICE_PROVIDER_PROFILE_CREATED("com.wks.servicemarketplace.auth.queue.serviceProviderCreated"),
        SERVICE_PROVIDER_PROFILE_CREATION_FAILED("com.wks.servicemarketplace.auth.queue.serviceProviderCreationFailed");

        fun declare(channel: Channel) {
            channel.queueDeclare(this.queueName, true, false, false, emptyMap());
        }
    }

    class RoutingKey {
        companion object {
            const val CUSTOMER_ACCOUNT_CREATED = "com.wks.servicemarketplace.auth.customer.created"
            const val SERVICE_PROVIDER_ACCOUNT_CREATED = "com.wks.servicemarketplace.auth.serviceProvider.created"
        }
    }
}