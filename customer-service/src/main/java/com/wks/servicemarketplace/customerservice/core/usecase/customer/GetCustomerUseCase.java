package com.wks.servicemarketplace.customerservice.core.usecase.customer;

import com.google.common.base.Preconditions;
import com.wks.servicemarketplace.common.CustomerUUID;
import com.wks.servicemarketplace.common.errors.CoreException;
import com.wks.servicemarketplace.common.errors.ErrorType;
import com.wks.servicemarketplace.customerservice.adapters.db.dao.DataSource;
import com.wks.servicemarketplace.customerservice.api.CustomerResponse;
import com.wks.servicemarketplace.customerservice.core.daos.CustomerDao;
import com.wks.servicemarketplace.customerservice.core.usecase.UseCase;
import com.wks.servicemarketplace.customerservice.core.utils.CloseableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.sql.Connection;
import java.util.Collections;

public class GetCustomerUseCase implements UseCase<CustomerUUID, CustomerResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetCustomerUseCase.class);

    private final DataSource dataSource;
    private final CustomerDao customerDao;

    @Inject
    public GetCustomerUseCase(DataSource dataSource,
                              CustomerDao customerDao) {
        this.dataSource = dataSource;
        this.customerDao = customerDao;
    }

    @Override
    public CustomerResponse execute(CustomerUUID request) {
        Preconditions.checkNotNull(request);

        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            return customerDao.findCustomerByUuid(connection, request)
                    .map(customer -> CustomerResponse.builder()
                            .uuid(customer.getUuid())
                            .externalId(customer.getExternalId())
                            .name(customer.getName())
                            .addresses(Collections.emptyList())
                            .version(customer.getVersion())
                            .build()
                    ).orElseThrow(() -> new CoreException(ErrorType.RESOURCE_NOT_FOUND, String.format("No customer with id %s", request), null, null));
        } catch (Exception e) {
            LOGGER.error("Failed to load customer", e);
            throw new CoreException(ErrorType.UNKNOWN, "Failed to load customer", null, null);
        } finally {
            CloseableUtils.close(connection);
        }
    }
}
