package com.wks.servicesmarketplace.jobservice.core.usecases.serviceorder

import com.wks.servicesmarketplace.jobservice.core.auth.User
import com.wks.servicesmarketplace.jobservice.core.utils.ModelValidator
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class ServiceOrderRequest private constructor(
        @field:NotNull
        val customerExternalId: Long?,
        @field:NotNull
        val addressExternalId: Long?,
        @field:NotNull
        val serviceCategoryId: Long?,
        @field:NotBlank
        val title: String?,
        @field:NotBlank
        val description: String?,
        @field:NotBlank
        val orderDateTime: String?,
        @field:NotNull
        val user: User?
) {
    companion object {
        fun create(customerExternalId: Long?,
                   addressExternalId: Long?,
                   serviceCategoryId: Long?,
                   title: String?,
                   description: String?,
                   orderDateTime: String?,
                   user: User?): ServiceOrderRequest {
            return ModelValidator.validate(ServiceOrderRequest(
                    customerExternalId,
                    addressExternalId,
                    serviceCategoryId,
                    title, description,
                    orderDateTime,
                    user
            ));
        }
    }
}
