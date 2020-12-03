package com.wks.servicesmarketplace.orderservice.config

class QueueName {
    companion object {
        const val CUSTOMER_ADDRESS_ADDED = "com.wks.servicemarketplace.serviceorders.queue.addressAdded";
        const val VERIFY_ADDRESS = "com.wks.servicemarketplace.account.queue.verifyAddress";
        const val ADDRESS_VERIFIED = "com.wks.servicemarketplace.account.queue.addressVerified";
        const val ADDRESS_VERIFICATION_FAILED = "com.wks.servicemarketplace.account.queue.addressVerificationFailed";
    }
}

class RoutingKey{
    companion object{
        const val CUSTOMER_ADDRESS_ADDED = "com.wks.servicemarketplace.customer.address.added"
    }
}