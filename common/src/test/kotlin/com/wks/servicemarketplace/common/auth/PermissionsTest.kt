package com.wks.servicemarketplace.common.auth

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class PermissionsTest{

    @Test
    fun `GIVEN permissions WHEN converted to string list THEN a list of permission names is returned`(){
        assertThat(Permissions.of(Permission.CREATE_CUSTOMER, Permission.CREATE_ORDER).toStringList())
                .isEqualTo(listOf("customer.create","order.create"))
    }

    @Test
    fun `GIVEN list of permission names WHEN converted to permission THEN permissions mapped to enum`(){
        assertThat(Permissions.of(listOf("customer.create","order.create")))
                .isEqualTo(Permissions.of(Permission.CREATE_CUSTOMER, Permission.CREATE_ORDER))
    }
}