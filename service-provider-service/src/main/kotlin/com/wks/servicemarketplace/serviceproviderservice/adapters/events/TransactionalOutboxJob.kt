package com.wks.servicemarketplace.serviceproviderservice.adapters.events

import com.wks.servicemarketplace.authservice.api.ClientCredentialsTokenSupplier
import com.wks.servicemarketplace.serviceproviderservice.core.OutboxDao
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.quartz.Scheduler
import org.quartz.simpl.SimpleJobFactory
import org.quartz.spi.TriggerFiredBundle
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

class TransactionalOutboxJob : Job {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(TransactionalOutboxJob::class.java)
    }

    lateinit var outboxDao: OutboxDao
    lateinit var messagePublisher: DefaultMessagePublisher
    lateinit var clientCredentialsTokenSupplier: ClientCredentialsTokenSupplier

    override fun execute(context: JobExecutionContext?) {
        LOGGER.info("PING!")
        Executors.newCachedThreadPool().submit {
            outboxDao.connection().use { conn ->

                val (token, messages) = clientCredentialsTokenSupplier.get()
                    .thenCombine(CompletableFuture.supplyAsync { outboxDao.fetchUnpublishedMessages(conn) }) { token, events -> Pair(token, events) }
                    .get()

                messages.forEach { message ->
                    try {
                        conn.autoCommit = false
                        messagePublisher.publish(token.accessToken, message)
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

class TransactionalOutboxJobFactory constructor(private val outboxDao: OutboxDao,
                                                        private val messagePublisher: DefaultMessagePublisher,
                                                        private val clientCredentialsTokenSupplier: ClientCredentialsTokenSupplier) : SimpleJobFactory() {

    override fun newJob(bundle: TriggerFiredBundle?, scheduler: Scheduler?): Job {
        return (super.newJob(bundle, scheduler) as TransactionalOutboxJob).also {
            it.outboxDao = outboxDao
            it.messagePublisher = messagePublisher
            it.clientCredentialsTokenSupplier = clientCredentialsTokenSupplier
        }
    }
}