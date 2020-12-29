package com.wks.servicemarketplace.authservice.adapters.events

import com.wks.servicemarketplace.authservice.config.ClientCredentialsTokenSupplier
import com.wks.servicemarketplace.authservice.core.EventDao
import com.wks.servicemarketplace.authservice.core.Token
import com.wks.servicemarketplace.authservice.core.events.EventEnvelope
import com.wks.servicemarketplace.authservice.core.events.EventPublisher
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.quartz.Scheduler
import org.quartz.simpl.SimpleJobFactory
import org.quartz.spi.TriggerFiredBundle
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.time.Clock
import java.time.OffsetDateTime
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.function.BiFunction
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

                val batchTime = OffsetDateTime.now(Clock.systemUTC())
                val (token, events) = clientCredentialsTokenSupplier.get()
                        .thenCombine(CompletableFuture.supplyAsync { eventDao.fetchUnpublishedEvents(conn) }) { token, events -> Pair(token, events) }
                        .get()

                var (success, failed) = Pair(0, 0)
                LOGGER.info("${events.size} event(s) pending publication. Batch time: $batchTime")
                events.forEach { event ->
                    try {
                        conn.autoCommit = false
                        eventPublisher.publish(token.accessToken, event)
                        eventDao.setPublished(conn, event.eventId)
                        conn.commit()
                        success++
                    } catch (e: Exception) {
                        failed++
                        LOGGER.error("Failed to publish event '{}'. ", event, e)
                        conn.rollback()
                    }
                }
                LOGGER.info("Completed publishing events that were fetched at $batchTime. Success: $success, Failed: $failed")
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