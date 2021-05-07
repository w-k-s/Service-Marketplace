package com.wks.servicemarketplace.common.ids

import com.wks.servicemarketplace.common.auth.UserType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class DefaultChecksumCalculatorTest{

    @Test
    fun `GIVEN a string WHEN checksum is calculated THEN checksum is deterministic`(){
        assertThat(DefaultChecksumCalculator().generate("ABCDEFGH")).isEqualTo("8")
        assertThat(DefaultChecksumCalculator().generate("01-01-2021108999798731")).isEqualTo("4")
    }

    @ParameterizedTest
    @EnumSource(value = UserType::class)
    fun `GIVEN a string with a check digit WHEN check digit is correct THEN validation is successful`(userType: UserType){
        repeat(10) {
            assertThat(DefaultChecksumCalculator().validate(UserId.generate(userType).value.toString())).isTrue()
        }
    }
}