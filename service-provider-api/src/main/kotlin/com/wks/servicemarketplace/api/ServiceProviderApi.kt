package com.wks.servicemarketplace.api

import com.wks.servicemarketplace.api.proto.Company
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ServiceProviderApi {

    companion object {
        //TODO: Can we have the same path but distinguish what to return based on the accept header?
        const val API_PATH_V1_ = "/api/v1/serviceproviders"
        const val PROTO_PATH_V1_ = "/proto/v1/serviceproviders"
        const val ENDPOINT_GET_COMPANY = "/company"
    }

    @GET(PROTO_PATH_V1_ + ENDPOINT_GET_COMPANY)
    fun companyFromUserId(@Query("user_uuid") userUUID: String): Call<Company?>
}