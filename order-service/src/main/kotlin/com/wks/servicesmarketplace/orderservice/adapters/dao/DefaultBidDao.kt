package com.wks.servicesmarketplace.orderservice.adapters.dao

import com.wks.servicemarketplace.common.CompanyUUID
import com.wks.servicesmarketplace.orderservice.core.Bid
import com.wks.servicesmarketplace.orderservice.core.BidDao
import com.wks.servicesmarketplace.orderservice.core.BidUUID
import com.wks.servicesmarketplace.orderservice.core.OrderUUID
import org.javamoney.moneta.FastMoney
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.mapper.RowMapper
import toUTCOffsetDateTime
import toUTCTimestamp

class DefaultBidDao(private val jdbi: Jdbi) : BidDao {

    companion object {
        private val bidMapper = RowMapper<Bid> { rs, _ ->
            Bid(
                BidUUID.fromString(rs.getString("bid_uuid")),
                OrderUUID.fromString(rs.getString("order_uuid")),
                CompanyUUID.fromString(rs.getString("company_uuid")),
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

    override fun findByCompanyId(companyUUID: CompanyUUID): Bid? {
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
                    """INSERT INTO bid (bid_uuid,order_uuid,company_uuid,price,note,version,created_by,created_date,last_modified_by,last_modified_date)
                        VALUES (?,?,?,?,?,?,?,?,?)
                    """.trimMargin(),
                    bid.uuid,
                    bid.orderUUID,
                    bid.companyUUID,
                    bid.price.toString(),
                    bid.note,
                    bid.version,
                    bid.createdBy,
                    bid.createdDate.toUTCTimestamp(),
                    bid.lastModifiedBy,
                    bid.lastModifiedDate.toUTCTimestamp()
            )
        } == 1
    }

    override fun update(bidUUID: BidUUID, version: Int, newBid: Bid): Boolean {
        return jdbi.inTransaction<Int,Exception> {
            it.execute(
                    """
                        UPDATE bid SET price = ?, note = ?, last_modified_by = ?, last_modified_date = ? WHERE bid_uuid = ? AND version = ? 
                    """.trimIndent(),
                    newBid.price.toString(),
                    newBid.note,
                    newBid.lastModifiedBy,
                    newBid.lastModifiedDate.toUTCTimestamp(),
                    bidUUID,
                    version
            )
        } == 1
    }
}