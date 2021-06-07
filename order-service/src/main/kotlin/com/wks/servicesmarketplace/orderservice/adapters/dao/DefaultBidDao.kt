package com.wks.servicesmarketplace.orderservice.adapters.dao

import com.wks.servicemarketplace.common.CompanyUUID
import com.wks.servicesmarketplace.orderservice.core.*
import org.javamoney.moneta.FastMoney
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.mapper.RowMapper
import toUTCOffsetDateTime
import toUTCTimestamp

class DefaultBidDao(private val jdbi: Jdbi) : BidDao {

    companion object {
        private val bidMapper = RowMapper<Bid> { rs, _ ->
            Bid(
                    BidId.of(rs.getLong("id")),
                BidUUID.fromString(rs.getString("uuid")),
                OrderId.of(rs.getLong("order_id")),
                CompanyId.of(rs.getLong("company_id")),
                FastMoney.parse(rs.getString("price")),
                rs.getString("note"),
                rs.getString("created_by"),
                rs.getTimestamp("created_date").toUTCOffsetDateTime(),
                rs.getString("last_modified_by"),
                rs.getTimestamp("last_modified_date")?.toUTCOffsetDateTime(),
                rs.getInt("version")
            )
        }
    }

    override fun nextBidId(): BidId {
        return jdbi.withHandle<Long, Exception> {
            it.select("SELECT nextval('bid_id')")
                    .mapTo(Long::class.java)
                    .one()
        }.let { BidId.of(it) }
    }

    override fun findByCompanyUUID(companyUUID: CompanyUUID): Bid? {
        return jdbi.withHandle<Bid, Exception> {
            it.select(
                    """SELECT bid_uuid,order_uuid,company_uuid,note,price,version,created_by,created_date,last_modified_by,last_modified_date FROM bid WHERE company_uuid = ? LIMIT 1"""",
                    companyUUID.value
            ).map(bidMapper)
                    .first()
        }
    }

    override fun save(bid: Bid): Boolean {
        return jdbi.withHandle<Int,Exception> {
            it.execute(
                    """INSERT INTO bid (id,uuid,order_id,company_id,price,note,version,created_by,created_date,last_modified_by,last_modified_date)
                        VALUES (?,?,?,?,?,?,?,?,?)
                    """.trimMargin(),
                    bid.id,
                    bid.uuid,
                    bid.orderId,
                    bid.companyId,
                    bid.price.toString(),
                    bid.note,
                    bid.version,
                    bid.createdBy,
                    bid.createdDate.toUTCTimestamp(),
                    bid.lastModifiedBy,
                    bid.lastModifiedDate?.toUTCTimestamp()
            )
        } == 1
    }

    override fun update(bidId: BidId, version: Int, newBid: Bid): Boolean {
        return jdbi.inTransaction<Int,Exception> {
            it.execute(
                    """
                        UPDATE bid SET price = ?, note = ?, last_modified_by = ?, last_modified_date = ? WHERE id = ? AND version = ? 
                    """.trimIndent(),
                    newBid.price.toString(),
                    newBid.note,
                    newBid.lastModifiedBy,
                    newBid.lastModifiedDate?.toUTCTimestamp(),
                    bidId,
                    version
            )
        } == 1
    }
}