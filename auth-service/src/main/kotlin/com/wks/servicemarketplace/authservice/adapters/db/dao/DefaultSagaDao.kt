package com.wks.servicemarketplace.authservice.adapters.db.dao

import com.wks.servicemarketplace.authservice.core.SagaDao
import com.wks.servicemarketplace.common.events.EventId
import com.wks.servicemarketplace.common.sagas.*
import org.jooq.DatePart
import org.jooq.impl.DSL
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.table
import java.sql.Connection
import java.time.OffsetDateTime
import javax.inject.Inject

class DefaultSagaDao @Inject constructor(dataSource: DataSource) : BaseDao(dataSource), SagaDao {

    override fun saveSaga(connection: Connection, saga: Saga): Boolean {
        return create(connection).insertInto(
            table("saga"),
            field("transaction_id"),
            field("saga_name"),
            field("aggregate_id"),
            field("aggregate_type"),
            field("state"),
            field("deadline"),
            field("event_id")
        ).values(
            saga.transactionId.toString(),
            saga.name,
            saga.aggregateId,
            saga.aggregateType,
            saga.state.stateName(),
            when (saga.deadline) {
                is DeadlineTime -> (saga.deadline as DeadlineTime).time
                is DeadlineAfter -> DSL.timestampAdd(
                    DSL.currentTimestamp(),
                    (saga.deadline as DeadlineAfter).duration.toSeconds(),
                    DatePart.SECOND
                )
                else -> DSL.castNull(OffsetDateTime::class.java)
            },
            saga.eventId.toString()
        ).execute() == 1
    }

    override fun <T : Saga.State> fetchSaga(
        connection: Connection,
        transaction: TransactionId,
        stateMapper: (String) -> T
    ): Saga {
        return create(connection).select(
            field("transaction_id"),
            field("saga_name"),
            field("aggregate_id"),
            field("aggregate_type"),
            field("state"),
            field("deadline"),
            field("event_id")
        ).from(table("saga"))
            .where(field("transaction_id").eq(transaction.toString()))
            .fetchOne {
                Saga(
                    TransactionId.fromString(it.get("transaction_id", String::class.java)),
                    it.get("saga_name", String::class.java),
                    it.get("aggregate_id", String::class.java),
                    it.get("aggregate_type", String::class.java),
                    stateMapper(it.get("state", String::class.java)),
                    DeadlineTime(it.get("deadline", OffsetDateTime::class.java)),
                    EventId.fromString(it.get("event_id", String::class.java))
                )
            }
    }

    override fun updateSaga(
        connection: Connection,
        transaction: TransactionId,
        updatedSaga: Saga,
        oldState: Saga.State
    ): Boolean {
        return create(connection).update(table("saga"))
            .set(field("state"), updatedSaga.state.stateName())
            .set(
                field("deadline"), when (updatedSaga.deadline) {
                    is DeadlineTime -> (updatedSaga.deadline as DeadlineTime).time
                    is DeadlineAfter -> DSL.timestampAdd(
                        DSL.currentTimestamp(),
                        (updatedSaga.deadline as DeadlineAfter).duration.toSeconds(),
                        DatePart.SECOND
                    )
                    else -> DSL.castNull(OffsetDateTime::class.java)
                }
            )
            .set(field("event_id"), updatedSaga.eventId.toString())
            .where(
                field("transaction_id").eq(transaction.toString())
                    .and(field("state").eq(oldState.stateName()))
            ).execute() == 1
    }
}