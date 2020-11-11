package com.wks.servicemarketplace.customerservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.wks.servicemarketplace.customerservice.adapters.auth.TokenValidator;
import com.wks.servicemarketplace.customerservice.adapters.events.DefaultCustomerEventsReceiver;
import com.wks.servicemarketplace.customerservice.core.usecase.address.verifyaddress.VerifyAddressUseCase;
import com.wks.servicemarketplace.customerservice.core.usecase.customer.CreateCustomerUseCase;
import org.glassfish.hk2.api.Factory;

import javax.inject.Inject;

public class DefaultCustomerEventsReceiverFactory implements Factory<DefaultCustomerEventsReceiver> {

    private DefaultCustomerEventsReceiver eventReceiver;

    @Inject
    public DefaultCustomerEventsReceiverFactory(VerifyAddressUseCase addressUseCase,
                                                CreateCustomerUseCase createCustomerUseCase,
                                                ObjectMapper objectMapper,
                                                TokenValidator tokenValidator,
                                                Channel channel){
        this.eventReceiver = new DefaultCustomerEventsReceiver(
                addressUseCase,
                createCustomerUseCase,
                tokenValidator,
                objectMapper,
                channel
        );
    }

    @Override
    public DefaultCustomerEventsReceiver provide() {
        return eventReceiver;
    }

    @Override
    public void dispose(DefaultCustomerEventsReceiver instance) {

    }
}
