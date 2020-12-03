package com.wks.servicesmarketplace.orderservice.core.exceptions

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

internal fun Map<String, List<String>>.toFormattedString(value: String = ",",
                                                         key: String = "\n"): String {
    return this.map { "${it.key}: ${it.value.joinToString(value)}" }.joinToString(key)
}