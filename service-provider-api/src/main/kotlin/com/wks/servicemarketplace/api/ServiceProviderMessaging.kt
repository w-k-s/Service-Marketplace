package com.wks.servicemarketplace.api

import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.Channel

class ServiceProviderMessaging {
    enum class Exchange(val exchangeName: String){
        MAIN("com.wks.servicemarketplace.serviceProvider.exchange");

        fun declare(channel: Channel){
            channel.exchangeDeclare(
                this.exchangeName,
                BuiltinExchangeType.TOPIC,
                true,
                false,
                false,
                emptyMap()
            )
        }
    }

    enum class RoutingKey(val value: String){
        SERVICE_PROVIDER_PROFILE_CREATED("com.wks.servicemarketplace.serviceProvider.profile.create.success"),
        SERVICE_PROVIDER_PROFILE_CREATION_FAILED("com.wks.servicemarketplace.serviceProvider.profile.create.failure"),
        COMPANY_CREATED("com.wks.servicemarketplace.serviceProvider.company.create.success"),
        COMPANY_CREATION_FAILED("com.wks.servicemarketplace.serviceProvider.company.create.failure");
    }
}