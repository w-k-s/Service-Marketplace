package com.wks.servicemarketplace.customerservice.config.schedulers;

import com.wks.servicemarketplace.customerservice.adapters.events.TransactionalOutboxJob;
import com.wks.servicemarketplace.customerservice.adapters.events.TransactionalOutboxJobFactory;
import com.wks.servicemarketplace.customerservice.config.ApplicationParameters;
import org.quartz.JobBuilder;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import javax.inject.Inject;

public class TransactionalOutboxJobScheduler {

    @Inject
    public static Scheduler create(ApplicationParameters applicationParameters, TransactionalOutboxJobFactory transactionalOutboxJobFactory) {
        try {
            final var scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.setJobFactory(transactionalOutboxJobFactory);

            var transactionalOutboxJob = JobBuilder.newJob(TransactionalOutboxJob.class)
                    .withIdentity("transactionalOutboxJob", "transactionalOutbox")
                    .build();

            var trigger = TriggerBuilder.newTrigger()
                    .withIdentity("transactionalOutboxTrigger", "transactionalOutbox")
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInMilliseconds(applicationParameters.getOutboxIntervalMillis()).repeatForever()
                    )
                    .build();

            scheduler.scheduleJob(transactionalOutboxJob, trigger);
            return scheduler;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
