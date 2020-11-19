package com.wks.servicemarketplace.serviceproviderservice.core.exceptions

import java.util.*

interface CoreException {
    val errorType: ErrorType
    val fields: Map<String, List<String>>
    val message: String?
}

data class UnauthorizedException(override val message: String) : CoreException, RuntimeException(message) {
    override val errorType = ErrorType.AUTHORIZATION
    override val fields: Map<String, List<String>> = emptyMap()
}

data class CountryCodeNotFoundException(private val code: String,
                                        private val iso: String)
    : CoreException, RuntimeException("'$code' is not a valid '$iso' code") {
    override val errorType = ErrorType.VALIDATION
    override val fields = mapOf("code" to listOf(code), "iso" to listOf(iso))
}

class ValidationException(fields: Map<String, List<String>>) : CoreException, RuntimeException(fields.toString(key = " ")) {
    override val errorType = ErrorType.VALIDATION
    override val fields: Map<String, List<String>> = Collections.unmodifiableMap(fields)
}

internal fun Map<String, List<String>>.toString(value: String = ",",
                                       key: String = "\n"): String {
    return this.map { it.value.joinToString(value) }.joinToString(key)
}