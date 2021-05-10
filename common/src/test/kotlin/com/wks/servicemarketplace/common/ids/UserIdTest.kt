package com.wks.servicemarketplace.common.ids

import com.wks.servicemarketplace.common.auth.UserType
import com.wks.servicemarketplace.common.errors.CoreException
import com.wks.servicemarketplace.common.errors.ErrorType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
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
    fun `GIVEN a user id string that does not match the pattern WHEN userId is parsed THEN validation exception is thrown`(){
        assertThatThrownBy {
            UserId.fromString("US-01-202100109281494350-1")
        }.isInstanceOf(CoreException::class.java)
            .matches { (it as CoreException).message == "Invalid User Id. UserId does not match expected format" }
            .matches { (it as CoreException).errorType == ErrorType.VALIDATION }
    }

    @Test
    fun `GIVEN a user id string that has a non-existent userType WHEN userId is parsed THEN validation exception is thrown`(){
        assertThatThrownBy {
            UserId.fromString("01-99-202100109281494350-0")
        }.isInstanceOf(CoreException::class.java)
            .matches { (it as CoreException).message == "Invalid UserId '01-99-202100109281494350-0'. UserType could not be parsed" }
            .matches { (it as CoreException).errorType == ErrorType.VALIDATION }
    }

    @Test
    fun `GIVEN a user id number that does not have the correct length WHEN userId is parsed THEN validation exception is thrown`(){
        assertThatThrownBy {
            UserId.fromNumber(BigInteger.ONE)
        }.isInstanceOf(CoreException::class.java)
            .matches { (it as CoreException).message == "'1' can not be formatted as a UserId. Invalid Length. Expected 22 or 23. Got: 1" }
            .matches { (it as CoreException).errorType == ErrorType.VALIDATION }
    }

    @Test
    fun `GIVEN a valid user id number WHEN userId is parsed THEN userId is created successfully from string`(){
        UserId.fromNumber(BigInteger("1012021001092814943501")).let {
            assertThat(it).isInstanceOf(UserId::class.java)
            assertThat(it.value).isEqualTo(BigInteger("1012021001092814943501"))
            assertThat(it.userType).isEqualTo(UserType.CUSTOMER)
        }
    }

    @ParameterizedTest
    @EnumSource(value = UserType::class)
    fun `GIVEN a userId String WHEN userId is created from string THEN userId is created successfully`(userType: UserType){
        UserId.generate(userType).let {
            assertThat(UserId.fromString(it.toString())).isEqualTo(it)
            assertThat(it.userType).isEqualTo(userType)
        }
    }
}