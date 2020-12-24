package com.wks.servicemarketplace.customerservice.adapters.events;

class RoutingKey {
    class Incoming {
        static final String CUSTOMER_ACCOUNT_CREATED = "com.wks.servicemarketplace.account.customer.created";
    }

    class Outgoing {
        static final String CUSTOMER_PROFILE_CREATED = "com.wks.servicemarketplace.customer.customercreated";
        static final String ADDRESS_ADDED = "com.wks.servicemarketplace.customer.address.added";
    }
}
