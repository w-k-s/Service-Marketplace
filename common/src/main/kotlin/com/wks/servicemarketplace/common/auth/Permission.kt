package com.wks.servicemarketplace.common.auth

import com.wks.servicemarketplace.common.errors.CoreException
import com.wks.servicemarketplace.common.errors.ErrorType
import java.util.*

enum class Permission(val value: String) {
    CREATE_ORDER("order.create"),
    DELETE_ORDER("order.delete"),
    EDIT_ORDER("order.edit"),
    VIEW_ORDER("order.view"),
    CREATE_BID("bid.create"),
    DELETE_BID("bid.delete"),
    VIEW_BID("bid.view"),
    EDIT_BID("bid.edit"),
    ACCEPT_BID("bid.accept"),
    CREATE_CUSTOMER("customer.create"),
    DELETE_CUSTOMER("customer.delete"),
    EDIT_CUSTOMER("customer.edit"),
    VIEW_CUSTOMER("customer.view"),
    CREATE_COMPANY("company.create"),
    CREATE_COMPANY_REPRESENTATIVE("comprep.create"),
    CREATE_ADDRESS("address.create"),
    DELETE_ADDRESS("address.delete"),
    EDIT_ADDRESS("address.edit"),
    VIEW_ADDRESS("address.view");

    companion object{
        fun of(value: String)
        = values().firstOrNull { it.value == value }
                ?: throw CoreException(ErrorType.VALIDATION, "No permission with value '$value'")
    }
}

data class Permissions(private val value: EnumSet<Permission>) : Iterable<Permission>, Set<Permission>{
    companion object {
        fun all() = Permissions(EnumSet.allOf(Permission::class.java))
        fun none() = Permissions(EnumSet.noneOf(Permission::class.java))
        fun of(vararg permissions: Permission) = Permissions(EnumSet.copyOf(permissions.toList()))
        fun of(permissionNames: List<String>) = permissionNames
                .map { Permission.of(it) }
                .toList()
                .let { Permissions(EnumSet.copyOf(it)) }
    }
    override fun iterator() = value.iterator()
    override val size = value.size
    override fun contains(element: Permission) = value.contains(element)
    override fun containsAll(elements: Collection<Permission>) = value.containsAll(elements)
    override fun isEmpty() = value.isEmpty()
    fun toStringList() = value.map { it.value }.toList().sorted()
}