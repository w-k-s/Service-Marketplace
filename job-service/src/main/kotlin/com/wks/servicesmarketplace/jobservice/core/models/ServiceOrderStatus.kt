package com.wks.servicesmarketplace.jobservice.core.models

enum class ServiceOrderStatus {
    INVALID,
    VERIFYING,
    REJECTING,
    REJECTED,
    CREATED,
    BIDS_RECEIVED,
    WITHDRAWING,
    WITHDRAWN,
    AUTHORIZING_PAYMENT,
    PAYMENT_REJECTED,
    SCHEDULED,
    CANCELLING,
    CANCELLED,
    CANCELLING_NO_SHOW,
    NO_SHOW,
    IN_PROGRESS,
    COMPLETED
}