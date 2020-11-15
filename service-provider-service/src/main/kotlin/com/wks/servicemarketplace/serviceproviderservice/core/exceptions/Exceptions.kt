package com.wks.servicemarketplace.serviceproviderservice.core.exceptions

interface CoreException {
    val errorType: ErrorType
    val fields: Map<String, List<String>>
    val message: String?
}

data class UnauthorizedException(override val message: String) : CoreException, RuntimeException(message) {
    override val errorType = ErrorType.AUTHORIZATION
    override val fields: Map<String, List<String>> = emptyMap()
}

fun Map<String, List<String>>.toString(value: String = ",",
                                       key: String = "\n"): String {
    return this.map { it.value.joinToString(value) }.joinToString(key)
}