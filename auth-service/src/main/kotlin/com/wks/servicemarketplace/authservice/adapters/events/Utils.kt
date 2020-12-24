package com.wks.servicemarketplace.authservice.adapters.events

/**
 * Make RabbitMQ's boolean parameters more readable
 */

class Durable {
    companion object {
        const val TRUE = true
        const val FALSE = false
    }
}

class Exclusive {
    companion object {
        const val TRUE = true
        const val FALSE = false
    }
}

class Internal {
    companion object {
        const val TRUE = true
        const val FALSE = false
    }
}

class AutoDelete {
    companion object {
        const val TRUE = true
        const val FALSE = false
    }
}