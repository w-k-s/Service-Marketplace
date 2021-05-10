package com.wks.servicemarketplace.common.ids

import com.wks.servicemarketplace.common.Service
import com.wks.servicemarketplace.common.auth.UserType
import com.wks.servicemarketplace.common.errors.CoreException
import com.wks.servicemarketplace.common.errors.ErrorType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.math.BigInteger

internal class OrderIdTest{

    @Test
    fun `GIVEN a valid order id string WHEN order is parsed THEN order is created successfully from string`(){
        OrderId.fromString("01-0100-202100109281494350-3").let {
            assertThat(it).isInstanceOf(OrderId::class.java)
            assertThat(it.value).isEqualTo(BigInteger("101002021001092814943503"))
            assertThat(it.service).isEqualTo(Service.HOUSE_KEEPING)
        }
    }

    @Test
    fun `GIVEN a order id string that does not match the pattern WHEN order is parsed THEN validation exception is thrown`(){
        assertThatThrownBy {
            OrderId.fromString("01-0100-202100109281494350-F")
        }.isInstanceOf(CoreException::class.java)
            .matches { (it as CoreException).message == "Invalid Order Id. Order does not match expected format" }
            .matches { (it as CoreException).errorType == ErrorType.VALIDATION }
    }

    @Test
    fun `GIVEN a order id string that has a non-existent service WHEN orderId is parsed THEN validation exception is thrown`(){
        assertThatThrownBy {
            OrderId.fromString("01-9999-202100109281494350-1")
        }.isInstanceOf(CoreException::class.java)
            .matches { (it as CoreException).message == "Invalid OrderId 01-9999-202100109281494350-1. Service does not exist" }
            .matches { (it as CoreException).errorType == ErrorType.VALIDATION }
    }

    @Test
    fun `GIVEN a order id number that does not have the correct length WHEN userId is parsed THEN validation exception is thrown`(){
        assertThatThrownBy {
            OrderId.fromNumber(BigInteger.ONE)
        }.isInstanceOf(CoreException::class.java)
            .matches { (it as CoreException).message == "'1' can not be formatted as a OrderId. Invalid Length. Expected 24 or 25. Got: 1" }
            .matches { (it as CoreException).errorType == ErrorType.VALIDATION }
    }

    @Test
    fun `GIVEN a valid order id number WHEN orderId is parsed THEN orderId is created successfully from string`(){
        OrderId.fromNumber(BigInteger("0101002021001092814943503")).let {
            assertThat(it).isInstanceOf(OrderId::class.java)
            assertThat(it.value).isEqualTo(BigInteger("0101002021001092814943503"))
            assertThat(it.service).isEqualTo(Service.HOUSE_KEEPING)
        }
    }

    @ParameterizedTest
    @EnumSource(value = Service::class)
    fun `GIVEN a orderId String WHEN orderId is created from string THEN orderId is created successfully`(service: Service){
        OrderId.generate(service).let {
            assertThat(OrderId.fromString(it.toString())).isEqualTo(it)
            assertThat(it.service).isEqualTo(service)
        }
    }
}