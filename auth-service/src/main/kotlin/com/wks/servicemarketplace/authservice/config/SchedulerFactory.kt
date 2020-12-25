package com.wks.servicemarketplace.authservice.config

import com.wks.servicemarketplace.authservice.adapters.events.TransactionalOutboxJob
import org.glassfish.hk2.api.Factory
import org.quartz.*
import org.quartz.impl.StdSchedulerFactory
import javax.inject.Inject


class SchedulerFactory @Inject constructor() : Factory<Scheduler> {

    val scheduler = StdSchedulerFactory.getDefaultScheduler().also {
        scheduleTransactionalOutbox(it)
    }

    private fun scheduleTransactionalOutbox(it: Scheduler) {
        val job = JobBuilder.newJob(TransactionalOutboxJob::class.java)
                .withIdentity("transactionalOutboxJob", "transactionalOutbox").build()

        val trigger: Trigger = TriggerBuilder.newTrigger()
                .withIdentity("transactionalOutboxTrigger", "transactionalOutbox")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(60).repeatForever())
                .build()

        it.scheduleJob(job, trigger)
    }

    override fun provide(): Scheduler = scheduler

    override fun dispose(instance: Scheduler?) {}
}