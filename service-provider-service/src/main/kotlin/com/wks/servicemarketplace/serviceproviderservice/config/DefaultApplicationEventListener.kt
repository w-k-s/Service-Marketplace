package com.wks.servicemarketplace.serviceproviderservice.config

import org.glassfish.jersey.server.monitoring.ApplicationEvent
import org.glassfish.jersey.server.monitoring.ApplicationEventListener
import org.glassfish.jersey.server.monitoring.RequestEvent
import org.quartz.Scheduler
import javax.inject.Inject

class DefaultApplicationEventListener @Inject constructor(
    private val scheduler: Scheduler,
    private val migration: DatabaseMigration
) : ApplicationEventListener {
    override fun onEvent(event: ApplicationEvent) {
        when (event.type) {
            ApplicationEvent.Type.INITIALIZATION_START -> migration.migrate()
            ApplicationEvent.Type.INITIALIZATION_FINISHED -> scheduler.start()
            ApplicationEvent.Type.DESTROY_FINISHED -> scheduler.shutdown()
        }
    }

    override fun onRequest(requestEvent: RequestEvent?) = null
}