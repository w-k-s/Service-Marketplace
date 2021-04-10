package com.wks.servicemarketplace.serviceproviderservice.adapters.events

import com.wks.servicemarketplace.authservice.api.ClientCredentialsTokenSupplier
import com.wks.servicemarketplace.serviceproviderservice.core.OutboxDao
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class TransactionOutboxRunnable(
    private val outboxDao: OutboxDao,
    private val messagePublisher: DefaultMessagePublisher,
    private val clientCredentialsTokenSupplier: ClientCredentialsTokenSupplier
) : Runnable {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(TransactionOutboxRunnable::class.java)
    }

    override fun run() {
        outboxDao.connection().use { conn ->

            val (token, messages) = clientCredentialsTokenSupplier.get()
                .thenCombine(CompletableFuture.supplyAsync { outboxDao.fetchUnpublishedMessages(conn) }) { token, events ->
                    Pair(
                        token,
                        events
                    )
                }
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

class TransactionalOutboxScheduler(
    outboxDao: OutboxDao,
    messagePublisher: DefaultMessagePublisher,
    clientCredentialsTokenSupplier: ClientCredentialsTokenSupplier
) {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(TransactionalOutboxScheduler::class.java)
    }

    private val executor = Executors.newScheduledThreadPool(1)
    private val task = TransactionOutboxRunnable(
        outboxDao,
        messagePublisher,
        clientCredentialsTokenSupplier
    )

    fun scheduleExecution(delayMillis: Long) {
        val taskWrapper = Runnable {
            LOGGER.info("PING!")
            task.run()
        }
        executor.scheduleWithFixedDelay(taskWrapper, delayMillis, delayMillis, TimeUnit.MILLISECONDS)
    }


    fun stop() {
        executor.shutdown()
        try {
            executor.awaitTermination(5, TimeUnit.MINUTES)
        } catch (e: InterruptedException) {
            LOGGER.error(e.message, e)
        }
    }
}