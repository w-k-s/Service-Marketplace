package com.wks.servicemarketplace.authservice.messaging

class AuthMessaging {
    class Exchange {
        companion object {
            const val MAIN = "com.wks.servicemarketplace.auth.exchange.main"
        }
    }

    class RoutingKey {
        companion object {
            const val CUSTOMER_ACCOUNT_CREATED = "com.wks.servicemarketplace.auth.customer.accountCreated"
            const val SERVICE_PROVIDER_ACCOUNT_CREATED = "com.wks.servicemarketplace.auth.customer.serviceProviderCreated"
        }
    }
}