package com.wks.servicemarketplace.serviceproviderservice.core

import java.util.*

enum class Service(val code: String) {
    HOUSE_KEEPING("CLEAN"),
    ELECTRICAL("ELCTRC");

    override fun toString() = code

    companion object {
        fun of(code: String) = values().first { it.code == code }
    }
}

class Services(services: List<Service>) : Iterable<Service> {
    private val services = EnumSet.copyOf(services)

    companion object {
        fun of(codes: List<String>) = Services(codes.map { Service.of(it) })
    }

    constructor(vararg services: Service) : this(services.asList())

    override fun iterator() = services.iterator()

    override fun equals(other: Any?): Boolean {
        return (other as? Services)?.services == services
    }

    override fun hashCode(): Int {
        return services?.hashCode() ?: 0
    }

    override fun toString() = this.services.map { it.code }.joinToString { ", " }
}
