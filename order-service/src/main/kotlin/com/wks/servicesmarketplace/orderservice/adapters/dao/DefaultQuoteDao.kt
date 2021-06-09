package com.wks.servicesmarketplace.orderservice.adapters.dao

import com.wks.servicemarketplace.common.CompanyId
import com.wks.servicemarketplace.common.CompanyUUID
import com.wks.servicesmarketplace.orderservice.core.*
import org.javamoney.moneta.FastMoney
import org.javamoney.moneta.function.MonetaryQueries
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.mapper.RowMapper
import toUTCOffsetDateTime
import toUTCTimestamp
import javax.money.Monetary

class DefaultQuoteDao(private val jdbi: Jdbi) : QuoteDao {

    companion object {
        private val bidMapper = RowMapper<Quote> { rs, _ ->
            Quote(
                    QuoteId.of(rs.getLong("id")),
                QuoteUUID.fromString(rs.getString("uuid")),
                OrderId.of(rs.getLong("order_id")),
                CompanyId(rs.getLong("company_id")),
                FastMoney.ofMinor(Monetary.getCurrency(rs.getString("currency")),rs.getLong("amount_minor_units")),
                rs.getString("note"),
                rs.getString("created_by"),
                rs.getTimestamp("created_date").toUTCOffsetDateTime(),
                rs.getString("last_modified_by"),
                rs.getTimestamp("last_modified_date")?.toUTCOffsetDateTime(),
                rs.getInt("version")
            )
        }
    }

    override fun nextQuoteId(): QuoteId {
        return jdbi.withHandle<Long, Exception> {
            it.select("SELECT nextval('bid_id')")
                    .mapTo(Long::class.java)
                    .one()
        }.let { QuoteId.of(it) }
    }

    override fun findByCompanyUUID(companyUUID: CompanyUUID): Quote? {
        return jdbi.withHandle<Quote, Exception> {
            it.select(
                    """SELECT bid_uuid,order_uuid,company_uuid,note,currency,amount_minor_units,version,created_by,created_date,last_modified_by,last_modified_date FROM bid WHERE company_uuid = ? LIMIT 1"""",
                    companyUUID.value
            ).map(bidMapper)
                    .first()
        }
    }

    override fun save(quote: Quote): Boolean {
        return jdbi.withHandle<Int,Exception> {
            it.execute(
                    """INSERT INTO bid (id,uuid,order_id,company_id,currency,amount_minor_units,note,version,created_by,created_date,last_modified_by,last_modified_date)
                        VALUES (?,?,?,?,?,?,?,?,?)
                    """.trimMargin(),
                    quote.id,
                    quote.uuid,
                    quote.orderId,
                    quote.companyId,
                    quote.price.currency.currencyCode,
                    quote.price.query(MonetaryQueries.convertMinorPart()),
                    quote.note,
                    quote.version,
                    quote.createdBy,
                    quote.createdDate.toUTCTimestamp(),
                    quote.lastModifiedBy,
                    quote.lastModifiedDate?.toUTCTimestamp()
            )
        } == 1
    }

    override fun update(quoteId: QuoteId, version: Int, newQuote: Quote): Boolean {
        return jdbi.inTransaction<Int,Exception> {
            it.execute(
                    """
                        UPDATE bid SET amount_minor_units = ?, note = ?, last_modified_by = ?, last_modified_date = ? WHERE id = ? AND version = ? AND currency = ?
                    """.trimIndent(),
                    newQuote.price.query(MonetaryQueries.convertMinorPart()),
                    newQuote.note,
                    newQuote.lastModifiedBy,
                    newQuote.lastModifiedDate?.toUTCTimestamp(),
                    quoteId,
                    version,
                    newQuote.price.currency
            )
        } == 1
    }
}