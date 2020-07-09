package com.wks.servicesmarketplace.jobservice.core.exceptions

data class UseCaseException(val errorType: ErrorType,
                            val description: String? = null,
                            val userInfo: Map<String, String> = emptyMap(),
                            val throwable: Throwable? = null) : Exception(description, throwable)