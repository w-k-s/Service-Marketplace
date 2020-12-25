package com.wks.servicemarketplace.authservice.adapters.events

import org.quartz.Job
import org.quartz.JobExecutionContext

class TransactionalOutboxJob : Job {
    override fun execute(context: JobExecutionContext?) {
        System.out.println("Doing ...")
    }
}