package com.wks.servicemarketplace.customerservice.adapters.events;

import com.wks.servicemarketplace.authservice.api.ClientCredentialsTokenSupplier;
import com.wks.servicemarketplace.common.auth.Token;
import com.wks.servicemarketplace.common.messaging.Message;
import com.wks.servicemarketplace.customerservice.adapters.utils.Pair;
import com.wks.servicemarketplace.customerservice.core.daos.OutboxDao;
import com.wks.servicemarketplace.customerservice.core.daos.TransactionUtils;
import com.wks.servicemarketplace.customerservice.core.utils.CloseableUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class TransactionalOutboxJob implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionalOutboxJob.class);

    private OutboxDao outboxDao;
    private ClientCredentialsTokenSupplier tokenSupplier;
    private DefaultMessagePublisher publisher;

    public void setOutboxDao(OutboxDao outboxDao) {
        this.outboxDao = outboxDao;
    }

    public void setPublisher(DefaultMessagePublisher publisher) {
        this.publisher = publisher;
    }

    public void setTokenSupplier(ClientCredentialsTokenSupplier tokenSupplier) {
        this.tokenSupplier = tokenSupplier;
    }

    @Override
    public void execute(JobExecutionContext context) {
        Executors.newCachedThreadPool().submit(() -> {
            Connection connection = null;
            try {
                connection = getConnectionOrThrow();
                final var pair = getTokenAndMessages(connection);
                for (Message message : pair.getRight()) {
                    try {
                        TransactionUtils.beginTransaction(connection);
                        publisher.publish(message, pair.getLeft().getAccessToken());
                        outboxDao.setMessagePublished(connection, message.getId());
                        connection.commit();
                    } catch (Exception e) {
                        TransactionUtils.rollback(connection);
                    }
                }
            } finally {
                CloseableUtils.close(connection);
            }
        });
    }

    private Connection getConnectionOrThrow() {
        try {
            return outboxDao.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Pair<Token, List<Message>> getTokenAndMessages(Connection connection) {
        try {
            return tokenSupplier.get().thenCombine(CompletableFuture.supplyAsync(() ->
                    outboxDao.fetchUnpublishedMessages(connection, 10)
            ), Pair::of).get();
        } catch (Exception e) {
            LOGGER.error("Failed to load token/messages", e);
            throw new RuntimeException(e);
        }
    }
}
