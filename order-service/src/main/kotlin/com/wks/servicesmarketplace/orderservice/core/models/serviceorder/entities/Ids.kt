package com.wks.servicesmarketplace.orderservice.core.models.serviceorder.entities

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import java.io.Serializable
import java.util.*
import javax.persistence.Embeddable

@Embeddable
data class OrderUUID internal constructor(@JsonValue var value: UUID) : Serializable {
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
data class CustomerId internal constructor(@JsonValue var value: Long) : Serializable {
    companion object {
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        @JvmStatic
        fun of(id: Number) = CustomerId(id.toLong())
    }

    override fun toString() = value.toString()
}

@Embeddable
data class AddressId internal constructor(@JsonValue var value: Long) : Serializable {
    companion object {
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        @JvmStatic
        fun of(id: Number): AddressId {
            return AddressId(id.toLong())
        }
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
