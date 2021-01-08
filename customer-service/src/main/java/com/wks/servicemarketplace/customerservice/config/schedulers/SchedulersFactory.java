package com.wks.servicemarketplace.customerservice.config.schedulers;

import com.wks.servicemarketplace.customerservice.adapters.events.TransactionalOutboxJobFactory;
import com.wks.servicemarketplace.customerservice.config.ApplicationParameters;
import org.glassfish.hk2.api.Factory;

import javax.inject.Inject;

public class SchedulersFactory implements Factory<Schedulers> {

    private final Schedulers schedulers;

    @Inject
    public SchedulersFactory(ApplicationParameters applicationParameters,
                             TransactionalOutboxJobFactory transactionalOutboxJobFactory) {
        schedulers = new Schedulers(
                TransactionalOutboxJobScheduler.create(applicationParameters, transactionalOutboxJobFactory)
        );
    }

    @Override
    public Schedulers provide() {
        return schedulers;
    }

    @Override
    public void dispose(Schedulers instance) {

    }
}
