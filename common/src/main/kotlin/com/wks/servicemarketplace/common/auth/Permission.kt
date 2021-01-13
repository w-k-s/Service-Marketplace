package com.wks.servicemarketplace.common.auth

enum class Permission(val value: String) {
    CREATE_ORDER("order.create"),
    DELETE_ORDER("order.delete"),
    EDIT_ORDER("order.edit"),
    VIEW_ORDER("order.view"),
    CREATE_BID("bid.create"),
    DELETE_BID("bid.delete"),
    VIEW_BID("bid.view"),
    EDIT_BID("bid.edit"),
    ACCEPT_BID("bid.accept"),
    CREATE_CUSTOMER("customer.create"),
    DELETE_CUSTOMER("customer.delete"),
    EDIT_CUSTOMER("customer.edit"),
    VIEW_CUSTOMER("customer.view"),
    CREATE_COMPANY("company.create"),
    CREATE_COMPANY_REPRESENTATIVE("comprep.create"),
    CREATE_ADDRESS("address.create"),
    DELETE_ADDRESS("address.delete"),
    EDIT_ADDRESS("address.edit"),
    VIEW_ADDRESS("address.view");
}