package com.wks.servicemarketplace.serviceproviderservice.core.usecase

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder
import com.wks.servicemarketplace.serviceproviderservice.core.*
import com.wks.servicemarketplace.serviceproviderservice.core.auth.Authentication
import com.wks.servicemarketplace.serviceproviderservice.core.exceptions.CoreRuntimeException
import com.wks.servicemarketplace.serviceproviderservice.core.exceptions.ErrorType
import com.wks.servicemarketplace.serviceproviderservice.core.utils.ModelValidator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import javax.validation.constraints.NotNull

class CreateCompanyRepresentativeUseCase(private val companyRepresentativeDao: CompanyRepresentativeDao) : UseCase<CreateCompanyRepresentativeRequest, CreateCompanyRepresentativeResponse> {

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(CreateCompanyRepresentativeUseCase::class.java)
    }

    override fun execute(input: CreateCompanyRepresentativeRequest): CreateCompanyRepresentativeResponse {
        companyRepresentativeDao.connection().use {
            it.autoCommit = false

            try {
                input.authentication.checkRole("comprep.create")

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

            } catch (e: CoreRuntimeException) {
                LOGGER.error("Failed to create company representative: ${e.message}", e)
                it.rollback()
                throw e
            } catch (e: Exception) {
                LOGGER.error("Failed to create company representative: ${e.message}", e)
                it.rollback()
                throw CoreRuntimeException(ErrorType.UNKNOWN, e)
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
            var phoneNumber: String?,
            @NotNull
            var authentication: Authentication?
    ) {
        fun uuid(uuid: String?): Builder {
            this.uuid = uuid
            return this
        }

        fun firstName(firstName: String?): Builder {
            this.firstName = firstName
            return this
        }

        fun lastName(lastName: String?): Builder {
            this.lastName = lastName
            return this
        }

        fun email(email: String?): Builder {
            this.email = email
            return this
        }

        fun phoneNumber(phoneNumber: String?): Builder {
            this.phoneNumber = phoneNumber
            return this
        }

        fun authentication(authentication: Authentication?): Builder {
            this.authentication = authentication
            return this
        }

        fun build(): CreateCompanyRepresentativeRequest {
            return ModelValidator.validate(this).let {
                CreateCompanyRepresentativeRequest(
                        UUID.fromString(this.uuid!!),
                        Name.of(this.firstName!!, this.lastName!!),
                        Email.of(this.email!!),
                        PhoneNumber.of(this.phoneNumber!!),
                        this.authentication!!
                )
            }
        }
    }
}

data class CreateCompanyRepresentativeResponse(private val externalId: CompanyRepresentativeId,
                                          private val uuid: CompanyRepresentativeUUID)