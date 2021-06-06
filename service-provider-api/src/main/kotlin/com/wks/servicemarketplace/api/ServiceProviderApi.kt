package com.wks.servicemarketplace.api

import com.wks.servicemarketplace.api.proto.Company
import com.wks.servicemarketplace.api.proto.ErrorResponse
import com.wks.servicemarketplace.common.*
import com.wks.servicemarketplace.common.errors.CoreException
import com.wks.servicemarketplace.common.errors.ErrorType
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.protobuf.ProtoConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

internal interface InternalServiceProviderApi {

    companion object {
        const val PROTO_PATH_V1_ = "/proto/v1/serviceproviders"
        const val ENDPOINT_GET_COMPANY = "/company"
    }

    @GET(PROTO_PATH_V1_ + ENDPOINT_GET_COMPANY)
    fun companyFromUserId(@Query("user_uuid") userUUID: String): Call<Company?>
}

data class InternalServiceProviderClient(private val baseUrl: String) {

    private val client = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(ProtoConverterFactory.create())
            .build()
            .create(InternalServiceProviderApi::class.java)

    fun companyFromUserId(userUUID: UserId): CompanyResponse {
        try {
            val response = client.companyFromUserId(userUUID.toString()).execute()
            when{
                response.isSuccessful && response.body() != null -> response.body()!!.let {
                    return CompanyResponse(
                            id = CompanyId(it.id.toLong()),
                            uuid = CompanyUUID.fromString(it.uuid),
                            name = it.name,
                            phone = PhoneNumber.of(it.phone),
                            email = Email.of(it.email),
                            logoUrl = it.logoUrl,
                            services = it.servicesList.toList().let { list -> Services.of(list) },
                            createdBy = CompanyRepresentativeUUID.fromString(it.createdBy),
                            createdDate = OffsetDateTime.ofInstant(Instant.ofEpochSecond(it.createdDate.seconds), ZoneOffset.UTC),
                            version = it.version.toLong(),
                            lastModifiedDate = it.lastModifiedDate?.seconds?.let{ seconds -> OffsetDateTime.ofInstant(Instant.ofEpochSecond(seconds), ZoneOffset.UTC) },
                            lastModifiedBy = it.lastModifiedBy
                    )
                }
                !response.isSuccessful && response.errorBody() != null -> response.errorBody()!!.let { errorBody ->
                    ErrorResponse.parseFrom(errorBody.bytes()).let { errorResponse ->
                        throw CoreException(
                                ErrorType.valueOf(errorResponse.errorType.name),
                                errorResponse.message,
                                details = errorResponse.infoMap
                        )
                    }
                }
                else -> throw CoreException(
                        ErrorType.EXTERNAL_SYSTEM,
                        "Failed to load company for user '$userUUID'. No data sent"
                )
            }
        } catch (e: Exception) {
            throw CoreException(
                    ErrorType.EXTERNAL_SYSTEM,
                    "Failed to load company for user '$userUUID",
                    e
            )
        }
    }
}
