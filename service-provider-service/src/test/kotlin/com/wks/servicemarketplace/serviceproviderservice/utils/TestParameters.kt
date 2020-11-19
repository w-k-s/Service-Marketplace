package com.wks.servicemarketplace.serviceproviderservice.utils

data class TestParameters constructor(
        val jdbcUrl: String = System.getenv("jdbcUrl_test"),
        val jdbcUsername: String = System.getenv("jdbcUsername_test"),
        val jdbcPassword: String = System.getenv("jdbcPassword_test")
)