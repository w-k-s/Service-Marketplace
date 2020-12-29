package com.wks.servicemarketplace.authservice.config

import com.wks.servicemarketplace.authservice.adapters.events.TransactionalOutboxJob
import com.wks.servicemarketplace.authservice.adapters.events.TransactionalOutboxJobFactory
import org.glassfish.hk2.api.Factory
import org.quartz.*
import org.quartz.impl.StdSchedulerFactory
import javax.inject.Inject


class TransactionalOutboxSchedulerFactory @Inject constructor(
        private val applicationParameters: ApplicationParameters,
        private val transactionalOutboxJobFactory: TransactionalOutboxJobFactory
) : Factory<Scheduler> {

    val scheduler: Scheduler = StdSchedulerFactory.getDefaultScheduler().also {
        it.setJobFactory(transactionalOutboxJobFactory)
        val transactionalOutboxJob = JobBuilder.newJob(TransactionalOutboxJob::class.java)
                .withIdentity("transactionalOutboxJob", "transactionalOutbox")
                .build()

        val trigger: Trigger = TriggerBuilder.newTrigger()
                .withIdentity("transactionalOutboxTrigger", "transactionalOutbox")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMilliseconds(applicationParameters.outboxIntervalMillis).repeatForever()
                )
                .build()

        it.scheduleJob(transactionalOutboxJob, trigger)
    }


    override fun provide(): Scheduler = scheduler

    override fun dispose(instance: Scheduler?) {}
}