package com.wks.servicemarketplace.serviceproviderservice.core.exceptions

import java.util.*

open class CoreRuntimeException(
        val errorType: ErrorType,
        override val message: String? = null,
        val fields: Map<String, List<String>> = emptyMap(),
) : RuntimeException(message) {

    constructor(errorType: ErrorType, exception: Exception)
            : this(errorType, exception.message)
}

open class CoreException(
        val errorType: ErrorType,
        override val message: String? = null,
        val fields: Map<String, List<String>> = emptyMap(),
) : Exception(message) {
    constructor(errorType: ErrorType, exception: Exception)
            : this(errorType, exception.message)
}

data class UnauthorizedException(override val message: String) : CoreRuntimeException(ErrorType.AUTHORIZATION, message)

data class CountryCodeNotFoundException(private val code: String,
                                        private val iso: String)
    : CoreRuntimeException(
        ErrorType.VALIDATION,
        "'$code' is not a valid '$iso' code",
        mapOf("code" to listOf(code), "iso" to listOf(iso))
)

class ValidationException(fields: Map<String, List<String>>) : CoreRuntimeException(
        ErrorType.VALIDATION,
        fields.toString(key = " "),
        Collections.unmodifiableMap(fields)
)

internal fun Map<String, List<String>>.toString(value: String = ",",
                                                key: String = "\n"): String {
    return this.map { it.value.joinToString(value) }.joinToString(key)
}