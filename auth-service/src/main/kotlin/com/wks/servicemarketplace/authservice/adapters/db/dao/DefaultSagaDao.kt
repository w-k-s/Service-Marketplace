package com.wks.servicemarketplace.authservice.adapters.db.dao

import com.wks.servicemarketplace.authservice.core.SagaDao
import com.wks.servicemarketplace.common.sagas.DeadlineAfter
import com.wks.servicemarketplace.common.sagas.DeadlineTime
import com.wks.servicemarketplace.common.sagas.Saga
import com.wks.servicemarketplace.common.sagas.TransactionId
import org.jooq.DatePart
import org.jooq.impl.DSL
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.table
import java.sql.Connection
import javax.inject.Inject

class DefaultSagaDao @Inject constructor(dataSource: DataSource) : BaseDao(dataSource), SagaDao {

    override fun saveSaga(connection: Connection, saga: Saga): Boolean {
        return create(connection).insertInto(table("saga"),
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
                when(saga.deadline){
                    is DeadlineTime -> (saga.deadline as DeadlineTime).time
                    is DeadlineAfter -> DSL.timestampAdd(DSL.currentTimestamp(), (saga.deadline as DeadlineAfter).duration.toSeconds(), DatePart.SECOND)
                    else -> null
                },
                saga.eventId.toString()
        ).execute() == 1
    }

    override fun updateSaga(connection: Connection,
                            transaction: TransactionId,
                            updatedSaga: Saga,
                            oldState: Saga.State): Boolean {
        return create(connection).update(table("saga"))
                .set(field("transaction_state"), updatedSaga.state.stateName())
                .set(field("deadline"), updatedSaga.deadline)
                .set(field("event_id"), updatedSaga.eventId.toString())
                .where(
                        field("transaction_id").eq(transaction.toString())
                                .and(field("state").eq(oldState.stateName()))
                ).execute() == 1
    }
}