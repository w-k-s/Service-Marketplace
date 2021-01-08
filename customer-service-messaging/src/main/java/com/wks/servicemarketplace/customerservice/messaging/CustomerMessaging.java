package com.wks.servicemarketplace.customerservice.messaging;

public class CustomerMessaging {
    public static class Exchange {
        public static final String MAIN = "com.wks.servicemarketplace.customer.exchange.main";
    }

    public static class RoutingKey {
        public static final String ADDRESS_ADDED = "com.wks.servicemarketplace.customer.addressAdded";
        public static final String CUSTOMER_PROFILE_CREATED = "com.wks.servicemarketplace.customer.profile.created";
        public static final String CUSTOMER_PROFILE_CREATION_FAILED = "com.wks.servicemarketplace.customer.profile.createFailed";
    }
}
