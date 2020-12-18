package com.wks.servicesmarketplace.orderservice.core

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.wks.servicesmarketplace.orderservice.core.repositories.UUIDConverter
import java.io.Serializable
import java.util.*
import javax.persistence.Convert
import javax.persistence.Embeddable

@Embeddable
data class OrderUUID internal constructor(@Convert(converter = UUIDConverter::class)
                                          @JsonValue
                                          var value: UUID
) : Serializable {
    companion object {
        fun of(uuid: UUID) = OrderUUID(uuid)

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        @JvmStatic
        fun fromString(uuidString: String) = OrderUUID(UUID.fromString(uuidString))
        fun random() = OrderUUID(UUID.randomUUID())
    }

    override fun toString() = value.toString()
}

@Embeddable
data class CustomerUUID internal constructor(@Convert(converter = UUIDConverter::class)
                                             @JsonValue var value: UUID) : Serializable {
    companion object {
        fun of(uuid: UUID) = OrderUUID(uuid)

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        @JvmStatic
        fun fromString(uuidString: String) = CustomerUUID(UUID.fromString(uuidString))
    }

    override fun toString() = value.toString()
}

@Embeddable
data class CompanyId internal constructor(private var value: Long) : Serializable {
    companion object {
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        @JvmStatic
        fun of(id: Number): CompanyId {
            return CompanyId(id.toLong())
        }
    }

    override fun toString() = value.toString()
}
