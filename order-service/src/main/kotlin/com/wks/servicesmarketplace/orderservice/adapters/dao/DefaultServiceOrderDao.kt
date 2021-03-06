import com.wks.servicemarketplace.common.*
import com.wks.servicesmarketplace.orderservice.core.*
import org.javamoney.moneta.FastMoney
import org.jdbi.v3.core.Jdbi
import java.security.Principal
import javax.money.Monetary

class DefaultServiceOrderDao(private val jdbi: Jdbi) : ServiceOrderDao {

    override fun nextOrderId(): OrderId {
        return jdbi.withHandle<Long, Exception> {
            it.select("SELECT nextval('server_order_id')")
                    .mapTo(Long::class.java)
                    .one()
        }.let { OrderId.of(it) }
    }

    override fun save(serviceOrder: ServiceOrder) {
        jdbi.withHandle<Int, Exception> {
            it.execute(
                    """
                INSERT INTO service_order 
                (id,uuid,customer_uuid,title,description,order_date_time,service_code,status,
                address_city,address_country,address_line1,address_line2,address_latitude,address_longitude,
                created_by,created_date,version)
                VALUES
                (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
                """.trimIndent(),
                    serviceOrder.id.value,
                    serviceOrder.uuid.toString(),
                    serviceOrder.customerUUID.value,
                    serviceOrder.title,
                    serviceOrder.description,
                    serviceOrder.orderDateTime.toUTCTimestamp(),
                    serviceOrder.serviceCode.code,
                    serviceOrder.status.name,
                    serviceOrder.address.city,
                    serviceOrder.address.country.toString(),
                    serviceOrder.address.line1,
                    serviceOrder.address.line2,
                    serviceOrder.address.latitude,
                    serviceOrder.address.longitude,
                    serviceOrder.createdBy.name,
                    serviceOrder.createdDate.toUTCTimestamp(),
                    serviceOrder.version
            )
        }
    }

    override fun findById(orderUUID: OrderUUID): ServiceOrder? {
        return jdbi.withHandle<ServiceOrder, Exception> {
            it.select("""
                SELECT id,uuid, customer_uuid, title, description, order_date_time, service_code, status,
                final_quote_currency, final_quote_amount_minor_units, reject_reason, scheduled_service_provider_id, address_city, address_country, address_line1,
                address_line2, address_latitude, address_longitude, created_by, created_date, last_modified_by, 
                last_modified_date 
                FROM service_order 
                WHERE uuid = ?
                """.trimIndent(),
                    orderUUID.toString()
            ).map { rs, _ ->
                val createdBy = rs.getString("created_by")
                val lastModifiedBy = rs.getString("last_modified_by")
                val currencyCode = rs.getString("final_quote_currency")
                val amountMinorUnits = rs.getLong("final_quote_minor_units")
                val finalQuote = currencyCode?.let{ code ->
                    FastMoney.ofMinor(Monetary.getCurrency(code), amountMinorUnits ?: 0)
                }

                ServiceOrder(
                        OrderId.of(rs.getLong("id")),
                        OrderUUID.fromString(rs.getString("uuid")),
                        CustomerUUID.fromString(rs.getString("customer_uuid")),
                        Service.of(rs.getString("service_code")),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getTimestamp("order_date_time").toUTCOffsetDateTime(),
                        Address(
                                rs.getString("address_line1"),
                                rs.getString("address_line2"),
                                rs.getString("address_city"),
                                CountryCode.of(rs.getString("address_country")),
                                rs.getBigDecimal("address_latitude"),
                                rs.getBigDecimal("address_longitude")
                        ),
                        ServiceOrderStatus.valueOf(rs.getString("status")),
                        rs.getLong("scheduled_service_provider_id")?.let { CompanyId(it) },
                        finalQuote,
                        rs.getString("reject_reason"),
                        rs.getTimestamp("created_date").toUTCOffsetDateTime(),
                        Principal { createdBy },
                        rs.getTimestamp("last_modified_date").toUTCOffsetDateTime(),
                        lastModifiedBy?.let { Principal { lastModifiedBy } }
                )
            }.firstOrNull()
        }
    }
}