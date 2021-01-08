package com.wks.servicemarketplace.customerservice.adapters.events;

import com.wks.servicemarketplace.authservice.api.ClientCredentialsTokenSupplier;
import com.wks.servicemarketplace.customerservice.core.daos.OutboxDao;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.simpl.SimpleJobFactory;
import org.quartz.spi.TriggerFiredBundle;

import javax.inject.Inject;

public class TransactionalOutboxJobFactory extends SimpleJobFactory {

    private final OutboxDao outboxDao;
    private final DefaultMessagePublisher defaultMessagePublisher;
    private final ClientCredentialsTokenSupplier tokenSupplier;

    @Inject
    public TransactionalOutboxJobFactory(OutboxDao outboxDao,
                                         DefaultMessagePublisher defaultMessagePublisher,
                                         ClientCredentialsTokenSupplier tokenSupplier) {
        super();
        this.outboxDao = outboxDao;
        this.defaultMessagePublisher = defaultMessagePublisher;
        this.tokenSupplier = tokenSupplier;
    }

    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler Scheduler) throws SchedulerException {
        final var job = (TransactionalOutboxJob) super.newJob(bundle, Scheduler);
        job.setOutboxDao(outboxDao);
        job.setPublisher(defaultMessagePublisher);
        job.setTokenSupplier(tokenSupplier);
        return job;
    }
}
