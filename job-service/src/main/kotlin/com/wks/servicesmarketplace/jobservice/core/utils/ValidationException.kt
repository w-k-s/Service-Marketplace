package com.wks.servicesmarketplace.jobservice.core.utils

import java.util.*

class ValidationException(fields: Map<String, String>) : RuntimeException(buildErrorMessage(fields)) {
    val fields: Map<String, String> = Collections.unmodifiableMap(fields)

    companion object {
        private fun buildErrorMessage(fields: Map<String, String>): String {
            return fields.entries
                    .map { "${it.key}: ${it.value}" }
                    .joinToString(",")
        }
    }

}