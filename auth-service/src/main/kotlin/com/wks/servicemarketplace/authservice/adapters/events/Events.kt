package com.wks.servicemarketplace.authservice.adapters.events

typealias ExchangeName = String
typealias QueueName = String
typealias RoutingKeyName = String

class Exchange {
    companion object {
        const val CUSTOMER: ExchangeName = "com.wks.servicemarketplace.customer.exchange"
        const val SERVICE_PROVIDER: ExchangeName = "com.wks.servicemarketplace.serviceProvider.exchange"
    }
}

class Incoming {
    class Queue {
        companion object {
            const val COMPANY_CREATED: QueueName = "com.wks.servicemarketplace.serviceProvider.company.created"
        }
    }
    class RoutingKey {
        companion object {
            const val COMPANY_CREATED: RoutingKeyName = "com.wks.servicemarketplace.serviceProvider.company.created"
        }
    }
}


class Outgoing{
    class RoutingKey {
        companion object {
            const val SERVICE_PROVIDER_CREATED: RoutingKeyName = "com.wks.servicemarketplace.auth.serviceProvider.created"
            const val CUSTOMER_CREATED: RoutingKeyName = "com.wks.servicemarketplace.auth.customer.created"
        }
    }
}

