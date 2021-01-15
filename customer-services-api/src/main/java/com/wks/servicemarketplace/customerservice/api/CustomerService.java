package com.wks.servicemarketplace.customerservice.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

import java.util.List;

public interface CustomerService {

    String PATH_V1_ = "/api/v1/customer";
    String ENDPOINT_GET_CUSTOMER = "";
    String ENDPOINT_ADD_ADDRESS = "/address";
    String ENDPOINT_GET_ADDRESSES = "/address";

    @POST(PATH_V1_ + ENDPOINT_GET_CUSTOMER)
    Call<CustomerResponse> getCustomer();

    @POST(PATH_V1_ + ENDPOINT_ADD_ADDRESS)
    Call<AddressResponse> addAddress(@Body AddressRequest address);

    @GET(PATH_V1_ + ENDPOINT_GET_ADDRESSES)
    Call<List<AddressResponse>> getAddresses();
}
