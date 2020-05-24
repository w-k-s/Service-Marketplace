package com.wks.servicesmarketplace.jobservice.adapters.web.error

import org.springframework.http.HttpStatus

enum class ErrorType(val code: Int, val httpStatus: HttpStatus) {
    VALIDATION(10100, HttpStatus.BAD_REQUEST)
}