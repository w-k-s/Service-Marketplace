package com.wks.servicemarketplace.customerservice.core.usecase.address;

import com.wks.servicemarketplace.common.CustomerUUID;
import com.wks.servicemarketplace.customerservice.api.AddressResponse;
import com.wks.servicemarketplace.customerservice.core.daos.CustomerDao;
import com.wks.servicemarketplace.customerservice.core.usecase.UseCase;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class FindAddressByCustomerUuidUseCase implements UseCase<CustomerUUID, List<AddressResponse>> {

    private final CustomerDao customerDao;

    @Inject
    public FindAddressByCustomerUuidUseCase(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    @Override
    public List<AddressResponse> execute(CustomerUUID customerUuid) {
        try {
            return customerDao.findAddressesByCustomerUUID(customerDao.getConnection(), customerUuid)
                    .stream()
                    .map(it -> AddressResponse.builder()
                            .uuid(it.getUuid())
                            .externalId(it.getExternalId())
                            .customerExternalId(it.getCustomerExternalId())
                            .name(it.getName())
                            .line1(it.getLine1())
                            .line2(it.getLine2())
                            .city(it.getCity())
                            .country(it.getCountry().toString())
                            .latitude(it.getLatitude())
                            .longitude(it.getLongitude())
                            .version(it.getVersion())
                            .build())
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
