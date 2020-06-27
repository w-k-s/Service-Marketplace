package com.wks.servicemarketplace.accountservice.config;

import com.wks.servicemarketplace.accountservice.core.daos.CustomerDao;
import com.wks.servicemarketplace.accountservice.core.events.CustomerEventsPublisher;
import com.wks.servicemarketplace.accountservice.core.usecase.address.verifyaddress.VerifyAddressUseCase;
import org.glassfish.hk2.api.Factory;

import javax.inject.Inject;

public class VerifyAddressUseCaseFactory implements Factory<VerifyAddressUseCase> {

    private VerifyAddressUseCase useCase;

    @Inject
    public VerifyAddressUseCaseFactory(CustomerDao customerDao, CustomerEventsPublisher customerEventsPublisher){
        this.useCase = new VerifyAddressUseCase(
                customerDao,
                customerEventsPublisher
        );
    }

    @Override
    public VerifyAddressUseCase provide() {
        return useCase;
    }

    @Override
    public void dispose(VerifyAddressUseCase instance) {

    }
}
