package com.wks.servicemarketplace.common.errors

enum class ErrorType(val code: Int) {
    VALIDATION(400),
    AUTHENTICATION(401),
    AUTHORIZATION(403),
    RESOURCE_NOT_FOUND(404),
    PROCESSING(428),
    UNKNOWN(500),
    EXTERNAL_SYSTEM(1501),
    NOT_UNIQUE(1401),
    INVALID_FORMAT(1402);
}

data class CoreException(
        val errorType: ErrorType,
        override val message: String? = null,
        override val cause: Throwable? = null,
        val details: Map<String, String>? = emptyMap()
) : RuntimeException(message, cause) {
    companion object {
        @JvmStatic
        fun validation(errors: Map<String, String>): CoreException {
            return CoreException(
                    ErrorType.VALIDATION,
                    errors.toFormattedString(),
                    null,
                    errors
            )
        }

        @JvmStatic
        fun unauthorized(missingPermission: String): CoreException {
            return CoreException(
                    ErrorType.AUTHORIZATION,
                    "User does not have permission $missingPermission"
            )
        }
    }
}

fun Map<String, String>.toFormattedString() = this.map { "${it.key}: ${it.value}" }.joinToString { "." }

fun ErrorType.toException(message: String? = null, cause: Throwable? = null, details: Map<String, String>? = emptyMap()): CoreException {
    return CoreException(this, message, cause, details)
}