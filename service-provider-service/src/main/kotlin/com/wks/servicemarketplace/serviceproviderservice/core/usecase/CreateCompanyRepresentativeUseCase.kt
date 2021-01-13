package com.wks.servicemarketplace.serviceproviderservice.core.usecase

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder
import com.wks.servicemarketplace.common.Email
import com.wks.servicemarketplace.common.ModelValidator
import com.wks.servicemarketplace.common.Name
import com.wks.servicemarketplace.common.PhoneNumber
import com.wks.servicemarketplace.common.auth.Authentication
import com.wks.servicemarketplace.common.auth.Permission
import com.wks.servicemarketplace.serviceproviderservice.core.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject
import javax.validation.constraints.NotNull

class CreateCompanyRepresentativeUseCase @Inject constructor(private val companyRepresentativeDao: CompanyRepresentativeDao) :
    UseCase<CreateCompanyRepresentativeRequest, CreateCompanyRepresentativeResponse> {

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(CreateCompanyRepresentativeUseCase::class.java)
    }

    override fun execute(input: CreateCompanyRepresentativeRequest): CreateCompanyRepresentativeResponse {
        companyRepresentativeDao.connection().use {
            it.autoCommit = false

            try {
                input.authentication.checkRole(Permission.CREATE_COMPANY_REPRESENTATIVE)

                val companyRepresentative = CompanyRepresentative(
                    0L,
                    companyRepresentativeDao.newCompanyRepresentativeId(it),
                    CompanyRepresentativeUUID(input.uuid),
                    input.name,
                    input.email,
                    input.phoneNumber,
                    input.authentication.name
                )

                companyRepresentativeDao.save(it, companyRepresentative)

                it.commit()

                return companyRepresentative.let { rep ->
                    CreateCompanyRepresentativeResponse(
                        rep.externalId,
                        rep.uuid
                    )
                }

            } catch (e: Exception) {
                LOGGER.error("Failed to create company representative: ${e.message}", e)
                it.rollback()
                throw e
            }
        }
    }
}

@JsonDeserialize(builder = CreateCompanyRepresentativeRequest.Builder::class)
data class CreateCompanyRepresentativeRequest(
    val uuid: UUID,
    val name: Name,
    val email: Email,
    val phoneNumber: PhoneNumber,
    val authentication: Authentication
) {
    @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "")
    class Builder(
        @NotNull
        var uuid: String?,
        @NotNull
        var firstName: String?,
        @NotNull
        var lastName: String?,
        @NotNull
        var email: String?,
        @NotNull
        var mobileNumber: String?,
        @NotNull
        var authentication: Authentication?
    ) {
        fun build(): CreateCompanyRepresentativeRequest {
            return ModelValidator.validate(this).let {
                CreateCompanyRepresentativeRequest(
                    UUID.fromString(this.uuid!!),
                    Name.of(this.firstName!!, this.lastName!!),
                    Email.of(this.email!!),
                    PhoneNumber.of(this.mobileNumber!!),
                    this.authentication!!
                )
            }
        }
    }
}

data class CreateCompanyRepresentativeResponse(
    private val externalId: CompanyRepresentativeId,
    private val uuid: CompanyRepresentativeUUID
)