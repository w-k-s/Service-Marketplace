import com.wks.servicemarketplace.common.CountryCode
import com.wks.servicemarketplace.common.CustomerUUID
import com.wks.servicemarketplace.common.Service
import com.wks.servicesmarketplace.orderservice.core.*
import org.javamoney.moneta.FastMoney
import org.jdbi.v3.core.Jdbi
import java.security.Principal

class DefaultServiceOrderDao(private val jdbi: Jdbi) : ServiceOrderDao {

    override fun save(serviceOrder: ServiceOrder) {
        jdbi.withHandle<Int, Exception> {
            it.execute(
                    """
                INSERT INTO service_order 
                (order_uuid,customer_uuid,title,description,order_date_time,service_code,status,
                address_city,address_country,address_line1,address_line2,address_latitude,address_longitude,
                created_by,created_date
                VALUES
                (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
                """.trimIndent(),
                    serviceOrder.orderUUID.toString(),
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
                    serviceOrder.createdDate.toUTCTimestamp()
            )
        }
    }

    override fun findById(orderUUID: OrderUUID): List<ServiceOrder> {
        return jdbi.withHandle<List<ServiceOrder>, Exception> {
            it.select("""
                SELECT order_uuid, customer_uuid, title, description, order_date_time, service_code, status,
                price, reject_reason, scheduled_service_provider_id, address_city, address_country, address_line1,
                address_line2, address_latitude, address_longitude, created_by, created_date, last_modified_by, 
                last_modified_date 
                FROM service_order 
                WHERE order_uuid = ?
                """.trimIndent(),
                    orderUUID.toString()
            ).map { rs,_ ->
                val createdBy = rs.getString("created_by")
                val lastModifiedBy = rs.getString("last_modified_by")
                ServiceOrder(
                       OrderUUID.fromString(rs.getString("order_uuid")),
                       CustomerUUID.fromString(rs.getString("customer_uuid")),
                        Service.of(rs.getString("service_code")),
                       rs.getString("title"),
                       rs.getString("title"),
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
                        rs.getLong("scheduled_service_provider_id")?.let { CompanyId.of(it) },
                        rs.getString("price")?.let { price -> FastMoney.parse(price) },
                        rs.getString("reject_reason"),
                        rs.getTimestamp("created_date").toUTCOffsetDateTime(),
                        Principal { createdBy },
                        rs.getTimestamp("last_modified_date").toUTCOffsetDateTime(),
                        lastModifiedBy?.let { Principal {lastModifiedBy} }
                )
            }.list()
        }
    }
}