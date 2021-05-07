package com.wks.servicemarketplace.common.ids

import com.wks.servicemarketplace.common.auth.UserType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigInteger

internal class UserIdTest{

    @Test
    fun `GIVEN a valid user id string WHEN userId is parsed THEN userId is created successfully from string`(){
        UserId.fromString("01-01-202100109281494350-1").let {
            assertThat(it).isInstanceOf(UserId::class.java)
            assertThat(it.value).isEqualTo(BigInteger("1012021001092814943501"))
            assertThat(it.userType).isEqualTo(UserType.CUSTOMER)
        }
    }

    @Test
    fun `GIVEN a user id string that does not match the pattern WHEN userId is parsed THEN RuntimeExceptionIsThrown`(){
        UserId.fromString("01-01-202100109281494350-1").let {
            assertThat(it).isInstanceOf(UserId::class.java)
            assertThat(it.value).isEqualTo(BigInteger("1012021001092814943501"))
            assertThat(it.userType).isEqualTo(UserType.CUSTOMER)
        }
    }
}