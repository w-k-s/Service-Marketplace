package com.wks.servicemarketplace.authservice.adapters.events

import com.wks.servicemarketplace.authservice.config.ClientCredentialsTokenSupplier
import com.wks.servicemarketplace.authservice.core.OutboxDao
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

    lateinit var outboxDao: OutboxDao
    lateinit var eventPublisher: DefaultEventPublisher
    lateinit var clientCredentialsTokenSupplier: ClientCredentialsTokenSupplier

    override fun execute(context: JobExecutionContext?) {
        Executors.newCachedThreadPool().submit {
            outboxDao.connection().use { conn ->

                val (token, messages) = clientCredentialsTokenSupplier.get()
                        .thenCombine(CompletableFuture.supplyAsync { outboxDao.fetchUnpublishedMessages(conn) }) { token, events -> Pair(token, events) }
                        .get()

                messages.forEach { message ->
                    try {
                        conn.autoCommit = false
                        eventPublisher.publish(token.accessToken, message)
                        outboxDao.setMessagePublished(conn, message.id)
                        conn.commit()
                    } catch (e: Exception) {
                        LOGGER.error("Failed to publish message '{}'. ", message, e)
                        conn.rollback()
                    }
                }
            }
        }
    }
}

class TransactionalOutboxJobFactory @Inject constructor(private val outboxDao: OutboxDao,
                                                        private val eventPublisher: DefaultEventPublisher,
                                                        private val clientCredentialsTokenSupplier: ClientCredentialsTokenSupplier) : SimpleJobFactory() {

    override fun newJob(bundle: TriggerFiredBundle?, scheduler: Scheduler?): Job {
        return (super.newJob(bundle, scheduler) as TransactionalOutboxJob).also {
            it.outboxDao = outboxDao
            it.eventPublisher = eventPublisher
            it.clientCredentialsTokenSupplier = clientCredentialsTokenSupplier
        }
    }

}