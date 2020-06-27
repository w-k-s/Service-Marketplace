package com.wks.servicemarketplace.accountservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.wks.servicemarketplace.accountservice.adapters.events.DefaultVerifyAddressEventReceiver;
import com.wks.servicemarketplace.accountservice.core.usecase.address.verifyaddress.VerifyAddressUseCase;
import org.glassfish.hk2.api.Factory;

import javax.inject.Inject;

public class DefaultVerifyAddressEventReceiverFactory implements Factory<DefaultVerifyAddressEventReceiver> {

    private DefaultVerifyAddressEventReceiver eventReceiver;

    @Inject
    public DefaultVerifyAddressEventReceiverFactory(VerifyAddressUseCase addressUseCase, ObjectMapper objectMapper, Channel channel){
        this.eventReceiver = new DefaultVerifyAddressEventReceiver(
                addressUseCase,
                objectMapper,
                channel
        );
    }

    @Override
    public DefaultVerifyAddressEventReceiver provide() {
        return eventReceiver;
    }

    @Override
    public void dispose(DefaultVerifyAddressEventReceiver instance) {

    }
}
