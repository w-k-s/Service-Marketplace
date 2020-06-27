package com.wks.servicesmarketplace.jobservice.core.sagas.verifyserviceorder;

import com.wks.servicesmarketplace.jobservice.core.events.AddressVerificationFailedEvent;
import com.wks.servicesmarketplace.jobservice.core.events.AddressVerifiedEvent;
import com.wks.servicesmarketplace.jobservice.core.events.EventPublisher;
import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.commands.RejectServiceOrderCommand;
import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.commands.VerifyServiceOrderCommand;
import com.wks.servicesmarketplace.jobservice.core.models.serviceorder.events.CreateServiceOrderEvent;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VerifyServiceOrderSaga {

    private static final Logger LOGGER = LoggerFactory.getLogger(VerifyServiceOrderSaga.class);

    private final transient EventPublisher eventPublisher;
    private final transient CommandGateway commandGateway;

    @Autowired
    public VerifyServiceOrderSaga(EventPublisher eventPublisher, CommandGateway commandGateway) {
        this.eventPublisher = eventPublisher;
        this.commandGateway = commandGateway;
    }

    @StartSaga(forceNew = true)
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(CreateServiceOrderEvent event) {
        LOGGER.info("CreateServiceOrderEvent '{}'", event);
        eventPublisher.publish(new VerifyAddressCommand(
                event.getOrderId(),
                event.getCustomerId(),
                event.getAddress().getExternalId(),
                event.getAddress().getLatitude(),
                event.getAddress().getLongitude(),
                event.getAddress().getVersion()
        ));
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(AddressVerifiedEvent event) {
        LOGGER.info("AddressVerifiedEvent '{}'", event);
        commandGateway.send(new VerifyServiceOrderCommand(
                event.getOrderId(),
                "John Doe"
        ));
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(AddressVerificationFailedEvent event) {
        LOGGER.info("AddressVerificationFailedEvent '{}'", event);
        commandGateway.send(new RejectServiceOrderCommand(
                event.getOrderId(),
                event.getType(),
                "John Doe"
        ));
    }
}
