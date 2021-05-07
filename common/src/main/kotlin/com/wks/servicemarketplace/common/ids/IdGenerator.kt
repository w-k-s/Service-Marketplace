package com.wks.servicemarketplace.common.ids

import com.wks.servicemarketplace.common.ids.IdDecorator.Companion.DEFAULT_BASE
import java.time.Clock
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

interface IdDecorator {
    companion object {
        const val DEFAULT_BASE = 36;
    }

    fun apply(value: String): String
}

class YearAppender(
    private val date: LocalDate = LocalDate.now(Clock.systemUTC()),
    private val base: Int = DEFAULT_BASE,
    private val padStart: Int = DEFAULT_PAD_START
) : IdDecorator {
    companion object{
        const val DEFAULT_PAD_START = 2
    }

    override fun apply(value: String): String {
        val token = Integer.parseInt(date.format(DateTimeFormatter.ofPattern("YYYY", Locale.US)))
            .toString(base)
            .padStart(padStart,'0')
        return "$value$token"
    }
}

class SecondsSinceStartOfYearAppender(
    private val date: LocalDateTime = LocalDateTime.now(Clock.systemUTC()),
    private val base: Int = DEFAULT_BASE,
    private val padStart: Int = DEFAULT_PAD_START
) : IdDecorator {

    companion object{
        const val DEFAULT_BASE = 10
        const val DEFAULT_PAD_START = 8
    }

    override fun apply(value: String): String {
        val now = date
        val startOfYear = LocalDate.ofYearDay(now.year, 1).atStartOfDay()
        val secondsSinceStartOfYear = Duration.between(startOfYear, now).seconds
        val token = secondsSinceStartOfYear
            .toString(base)
            .padStart(padStart, '0')
        return "$value$token"
    }

}

class RandomNumberAppender(
    private val numberOfDigits: Int = DEFAULT_MINIMUM_NUMBER_OF_DIGITS,
    private val base: Int = DEFAULT_BASE
) : IdDecorator {
    companion object {
        private const val DEFAULT_MINIMUM_NUMBER_OF_DIGITS = 4
    }

    override fun apply(value: String): String {
        val token =  (0..9).shuffled().take(numberOfDigits)
            .map { it.toString(base) }
            .joinToString("")
            .padStart(numberOfDigits, '0')
        return "$value$token"
    }
}

class StringAppender(private val token: String) : IdDecorator {
    override fun apply(value: String) = "$value$token"
}


class ChecksumAppender(private val generator: ChecksumGenerator) : IdDecorator{
    override fun apply(value: String) = "$value${generator.generate(value)}"
}

class IdGenerator private constructor(private val decorators: List<IdDecorator>){
    companion object{
        fun create(vararg decorators: IdDecorator) = IdGenerator(decorators.toList())
    }

    fun generate(): String{
        var value = ""
        decorators.forEach {
            value = it.apply(value)
        }
        return value
    }
}
