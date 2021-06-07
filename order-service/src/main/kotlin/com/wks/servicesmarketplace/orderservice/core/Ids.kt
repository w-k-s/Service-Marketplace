package com.wks.servicesmarketplace.orderservice.core

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import java.io.Serializable
import java.util.*

data class OrderId internal constructor(private var value: Long) : Serializable {
    companion object {
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        @JvmStatic
        fun of(id: Number): OrderId {
            return OrderId(id.toLong())
        }
    }

    override fun toString() = value.toString()
}

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

data class BidId internal constructor(private var value: Long) : Serializable {
    companion object {
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        @JvmStatic
        fun of(id: Number): BidId {
            return BidId(id.toLong())
        }
    }
    override fun toString() = value.toString()
}

data class BidUUID internal constructor(@JsonValue var value: UUID) : Serializable {
    companion object {
        fun of(uuid: UUID) = BidUUID(uuid)

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        @JvmStatic
        fun fromString(uuidString: String) = BidUUID(UUID.fromString(uuidString))
        fun random() = BidUUID(UUID.randomUUID())
    }

    override fun toString() = value.toString()
}
