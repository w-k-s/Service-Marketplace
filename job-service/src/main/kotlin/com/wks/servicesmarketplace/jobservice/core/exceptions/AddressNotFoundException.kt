package com.wks.servicesmarketplace.jobservice.core.exceptions

data class AddressNotFoundException(val addressExternalId: Long,
                                    val customerExternalId: Long)
    : Exception("Could not find address '${addressExternalId}' for customer '${customerExternalId}'")