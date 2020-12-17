package com.wks.servicesmarketplace.orderservice.core.exceptions

import com.wks.servicesmarketplace.orderservice.core.OrderUUID

open class CoreException(val errorType: ErrorType,
                         val description: String? = null,
                         val userInfo: Map<String, List<String>> = emptyMap(),
                         private val throwable: Throwable? = null) : Exception(description, throwable)

open class CoreRuntimeException(val errorType: ErrorType,
                                val description: String? = null,
                                val userInfo: Map<String, List<String>> = emptyMap(),
                                private val throwable: Throwable? = null) : RuntimeException(description, throwable)


class ValidationException(fields: Map<String, List<String>>) : CoreRuntimeException(
        ErrorType.VALIDATION,
        fields.toFormattedString(key = " "),
        fields
)

class CountryNotFoundException(countryCode: String, iso: String)
    : CoreRuntimeException(ErrorType.INVALID_COUNTRY, "$countryCode is not a valid $iso country code")

class ServiceOrderNotFoundException(orderId: OrderUUID)
    : CoreException(ErrorType.SERVICE_ORDER_NOT_FOUND, "service order $orderId does not exist")

class UserIdMissingException : CoreRuntimeException(ErrorType.USER_ID_MISSING, "Token does not container user id")

internal fun Map<String, List<String>>.toFormattedString(value: String = ",",
                                                         key: String = "\n"): String {
    return this.map { "${it.key}: ${it.value.joinToString(value)}" }.joinToString(key)
}