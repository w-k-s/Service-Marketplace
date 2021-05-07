package com.wks.servicemarketplace.common.ids

import com.fasterxml.jackson.annotation.JsonValue
import com.wks.servicemarketplace.common.auth.UserType
import com.wks.servicemarketplace.common.errors.CoreException
import com.wks.servicemarketplace.common.errors.ErrorType
import java.math.BigInteger
import java.util.*


data class UserId private constructor(@JsonValue val value: BigInteger) {
    companion object {
        private const val VERSION = 1
        private val PATTERN = Regex("(1|01)-\\d{2}-\\d{18}-\\d")
        private val ILLEGAL_CHARACTERS = Regex("[^A-Za-z\\d]")

        @JvmStatic
        fun fromString(userIdString: String) = userIdString
            .takeIf { userIdString.matches(PATTERN) }
            ?.takeIf { DefaultChecksumCalculator().validate(it) }
            ?.let { it.replace(ILLEGAL_CHARACTERS, "") }
            ?.let { BigInteger(it) }
            ?.let { UserId(it) }
            ?: throw CoreException(ErrorType.VALIDATION, "Invalid UserId")

        @JvmStatic
        fun generate(userType: UserType): UserId {
            return IdGenerator.create(
                StringAppender(VERSION.toString().padStart(2, '0')),
                StringAppender("-"),
                StringAppender(userType.numericCode.toString().padStart(2, padChar = '0')),
                StringAppender("-"),
                YearAppender(base = 10, padStart = 4),
                SecondsSinceStartOfYearAppender(base = 10, padStart = 10),
                RandomNumberAppender(base = 10, numberOfDigits = 4),
                StringAppender("-"),
                ChecksumAppender(DefaultChecksumCalculator())
            ).generate()
                .let { it.replace(ILLEGAL_CHARACTERS, "") }
                .let { BigInteger(it) }
                .let { UserId(it) }
        }

        @JvmStatic
        @Deprecated("Use generate", ReplaceWith("UserId.generate(userType: UserType)"))
        fun of(uuid: UUID): UserId = throw UnsupportedOperationException("UserId can not be a uuid")

        @JvmStatic
        @Deprecated("", ReplaceWith("UserId.generate(userType: UserType)"))
        fun random(): UserId =
            throw UnsupportedOperationException("UserId can not be randomly generated. UserType is required")

    }

    val userType: UserType
        get() {
            return this.value.toString().let {
                when (it.length) {
                    22 -> "0$it"
                    23 -> it
                    else -> throw RuntimeException("UserId was created with unexpected length of ${it.length} digits. Expected 22 or 23")
                }
            }.subSequence(startIndex = 2, endIndex = 4)
                .let { Integer.parseInt(it.toString()) }
                .let { numericCode -> UserType.values().firstOrNull() { it.numericCode == numericCode } }
                ?: throw RuntimeException("Invalid UserId created ${this.value}. UserType could not be parsed")
        }

    override fun toString(): String{
        return this.value.toString().let {
            when (it.length) {
                22 -> "0$it"
                23 -> it
                else -> throw RuntimeException("UserId was created with unexpected length of ${it.length} digits. Expected 22 or 23")
            }
        }.let {
            "${it.subSequence(0,2)}-${it.subSequence(2,4)}-${it.subSequence(4,22)}-${it.subSequence(22,23)}"
        }
    }
}