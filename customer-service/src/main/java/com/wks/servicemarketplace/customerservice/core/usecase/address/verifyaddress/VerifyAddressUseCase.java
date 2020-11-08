package com.wks.servicemarketplace.customerservice.core.usecase.address.verifyaddress;

import com.google.common.collect.ImmutableMap;
import com.wks.servicemarketplace.customerservice.core.daos.CustomerDao;
import com.wks.servicemarketplace.customerservice.core.events.CustomerEventsPublisher;
import com.wks.servicemarketplace.customerservice.core.models.Address;
import com.wks.servicemarketplace.customerservice.core.usecase.UseCase;
import com.wks.servicemarketplace.customerservice.core.usecase.UseCaseException;
import com.wks.servicemarketplace.customerservice.core.usecase.errors.ErrorType;
import com.wks.servicemarketplace.customerservice.core.utils.CloseableUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class VerifyAddressUseCase implements UseCase<VerifyAddressRequest, Void> {

    private final CustomerDao customerDao;
    private final CustomerEventsPublisher customerEventsPublisher;

    @Inject
    public VerifyAddressUseCase(CustomerDao customerDao,
                                CustomerEventsPublisher customerEventsPublisher) {
        this.customerDao = customerDao;
        this.customerEventsPublisher = customerEventsPublisher;
    }

    @Override
    public Void execute(VerifyAddressRequest request) throws UseCaseException, IOException {
        final Map<String, String> userInfo = ImmutableMap.of(
                "orderId", request.getOrderId(),
                "customerId", request.getCustomerExternalId().toString(),
                "addressId", request.getAddressExternalId().toString()
        );

        Connection connection = null;
        try {
            connection = customerDao.getConnection();
            final Address address = customerDao.findAddressByAddressIdAndCustomerId(connection, request.getCustomerExternalId(), request.getAddressExternalId())
                    .orElseThrow(() -> new UseCaseException(ErrorType.ADDRESS_NOT_FOUND, userInfo));

            final boolean addressVerified = address.getLatitude().equals(request.getAddressLatitude())
                    && address.getLongitude().equals(request.getAddressLongitude())
                    && address.getVersion().equals(request.getAddressVersion());

            if (!addressVerified) {
                throw new UseCaseException(ErrorType.ADDRESS_OUTDATED, userInfo);
            }

            customerEventsPublisher.addressVerified(new AddressVerifiedEvent(
                    request.getOrderId(),
                    request.getCustomerExternalId(),
                    request.getAddressExternalId()
            ));
        } catch (UseCaseException e) {
            customerEventsPublisher.addressVerificationFailed(new AddressVerificationFailedEvent(
                    request.getOrderId(),
                    e.getErrorType().code,
                    e.getErrorType().name(),
                    e.getDescription(),
                    e.getUserInfo()
            ));
            throw e;
        } catch (SQLException exception) {
            customerEventsPublisher.addressVerificationFailed(new AddressVerificationFailedEvent(
                    request.getOrderId(),
                    ErrorType.DATABASE.code,
                    ErrorType.DATABASE.name(),
                    exception.getMessage(),
                    userInfo
            ));
            throw new UseCaseException(ErrorType.DATABASE, exception.getMessage(), userInfo, exception);
        } finally {
            CloseableUtils.close(connection);
        }
        return null;
    }
}
