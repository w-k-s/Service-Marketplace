package com.wks.servicemarketplace.authservice.adapters.events

import com.wks.servicemarketplace.authservice.config.ClientCredentialsTokenSupplier
import com.wks.servicemarketplace.authservice.core.EventDao
import com.wks.servicemarketplace.authservice.core.events.EventPublisher
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.quartz.Scheduler
import org.quartz.simpl.SimpleJobFactory
import org.quartz.spi.TriggerFiredBundle
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import javax.inject.Inject

class TransactionalOutboxJob : Job {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(TransactionalOutboxJob::class.java)
    }

    lateinit var eventDao: EventDao
    lateinit var eventPublisher: EventPublisher
    lateinit var clientCredentialsTokenSupplier: ClientCredentialsTokenSupplier

    override fun execute(context: JobExecutionContext?) {
        Executors.newCachedThreadPool().submit {
            eventDao.connection().use { conn ->

                val (token, events) = clientCredentialsTokenSupplier.get()
                        .thenCombine(CompletableFuture.supplyAsync { eventDao.fetchUnpublishedEvents(conn) }) { token, events -> Pair(token, events) }
                        .get()

                events.forEach { event ->
                    try {
                        conn.autoCommit = false
                        eventPublisher.publish(token.accessToken, event)
                        eventDao.setPublished(conn, event.eventId)
                        conn.commit()
                    } catch (e: Exception) {
                        LOGGER.error("Failed to publish event '{}'. ", event, e)
                        conn.rollback()
                    }
                }
            }
        }
    }
}

class TransactionalOutboxJobFactory @Inject constructor(private val eventDao: EventDao,
                                                        private val eventPublisher: EventPublisher,
                                                        private val clientCredentialsTokenSupplier: ClientCredentialsTokenSupplier) : SimpleJobFactory() {

    override fun newJob(bundle: TriggerFiredBundle?, scheduler: Scheduler?): Job {
        return (super.newJob(bundle, scheduler) as TransactionalOutboxJob).also {
            it.eventDao = eventDao
            it.eventPublisher = eventPublisher
            it.clientCredentialsTokenSupplier = clientCredentialsTokenSupplier
        }
    }

}