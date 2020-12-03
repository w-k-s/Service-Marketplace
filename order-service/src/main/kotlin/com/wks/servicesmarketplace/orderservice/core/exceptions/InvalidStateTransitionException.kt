package com.wks.servicesmarketplace.orderservice.core.exceptions

import org.axonframework.commandhandling.CommandExecutionException
import kotlin.reflect.KClass

data class InvalidStateTransitionException(val aggregate: KClass<*>, val from: String, val to: String)
    : CommandExecutionException(
        "${aggregate.simpleName} can not transition from state '${from}' to '${to}'",
        IllegalStateException("${aggregate.simpleName} can not transition from state '${from}' to '${to}'")
)