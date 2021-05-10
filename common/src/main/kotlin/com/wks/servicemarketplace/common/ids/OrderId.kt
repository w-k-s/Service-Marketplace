package com.wks.servicemarketplace.common.ids

import com.fasterxml.jackson.annotation.JsonValue
import com.wks.servicemarketplace.common.Service
import com.wks.servicemarketplace.common.errors.CoreException
import com.wks.servicemarketplace.common.errors.ErrorType
import java.math.BigInteger


data class OrderId private constructor(@JsonValue val value: BigInteger) {
    companion object {
        private const val VERSION = 1
        private val PATTERN = Regex("(1|01)-\\d{4}-\\d{18}-\\d")
        private val ILLEGAL_CHARACTERS = Regex("[^A-Za-z\\d]")

        @JvmStatic
        fun fromString(orderIdString: String) = orderIdString
            .let {
                when (it.matches(PATTERN)) {
                    true -> it
                    else -> throw CoreException(
                        ErrorType.VALIDATION,
                        "Invalid Order Id. Order does not match expected format"
                    )
                }
            }.let {
                when (DefaultChecksumCalculator().validate(it)) {
                    true -> it
                    else -> throw CoreException(ErrorType.VALIDATION, "Invalid Order Id. Check sum validation failed")
                }
            }
            .let { it.replace(ILLEGAL_CHARACTERS, "") }
            .let { BigInteger(it) }
            .let { OrderId(it) }
            .also { it.service } // throws exception if service is unrecognized

        @JvmStatic
        fun fromNumber(userId: Number): OrderId {
            return userId.toString()
                .fixOrderIdStringLength()
                .hyphenateOrderIdString()
                .let { OrderId.fromString(it) }
        }

        @JvmStatic
        fun generate(service: Service): OrderId {
            return IdGenerator.create(
                StringAppender(VERSION.toString().padStart(2, '0')),
                StringAppender("-"),
                StringAppender(service.numericCode.toString().padStart(4, padChar = '0')),
                StringAppender("-"),
                YearAppender(base = 10, padStart = 4),
                SecondsSinceStartOfYearAppender(base = 10, padStart = 10),
                RandomNumberAppender(base = 10, numberOfDigits = 4),
                StringAppender("-"),
                ChecksumAppender(DefaultChecksumCalculator())
            ).generate()
                .let { it.replace(ILLEGAL_CHARACTERS, "") }
                .let { BigInteger(it) }
                .let { OrderId(it) }
        }
    }

    val service: Service
        get() {
            return this.value.toString()
                .fixOrderIdStringLength()
                .subSequence(startIndex = 2, endIndex = 6)
                .let { Integer.parseInt(it.toString()) }
                .let { numericCode -> Service.values().firstOrNull() { it.numericCode == numericCode } }
                ?: throw CoreException(ErrorType.VALIDATION, "Invalid OrderId ${this}. Service does not exist")
        }

    override fun toString(): String {
        return this.value.toString()
            .fixOrderIdStringLength()
            .hyphenateOrderIdString()
    }
}

private fun String.fixOrderIdStringLength() = this.let {
    when (it.length) {
        24 -> "0$it"
        25 -> it
        else -> throw CoreException(
            ErrorType.VALIDATION,
            "'${this}' can not be formatted as a OrderId. Invalid Length. Expected 24 or 25. Got: ${it.length}"
        )
    }
}

private fun String.hyphenateOrderIdString() =
    "${this.subSequence(0, 2)}-${this.subSequence(2, 6)}-${this.subSequence(6, 24)}-${this.subSequence(24, 25)}"