package com.wks.servicemarketplace.authservice.messaging

import com.rabbitmq.client.Channel

class AuthMessaging {
    class Exchange {
        companion object {
            const val MAIN = "com.wks.servicemarketplace.auth.exchange.main"
        }
    }

    enum class Queue(val queueName: String) {
        CUSTOMER_PROFILE_CREATED("com.wks.servicemarketplace.auth.queue.customerCreated"),
        CUSTOMER_PROFILE_CREATION_FAILED("com.wks.servicemarketplace.auth.queue.customerCreationFailed");

        fun declare(channel: Channel){
            channel.queueDeclare(this.queueName, true, false, false, emptyMap())
        }
    }

    class RoutingKey {
        companion object {
            const val CUSTOMER_ACCOUNT_CREATED = "com.wks.servicemarketplace.auth.customer.created"
            const val SERVICE_PROVIDER_ACCOUNT_CREATED = "com.wks.servicemarketplace.auth.serviceProvider.created"
        }
    }
}