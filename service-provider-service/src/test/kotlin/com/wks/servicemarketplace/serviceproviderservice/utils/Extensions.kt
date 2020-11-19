package com.wks.servicemarketplace.serviceproviderservice.utils

import com.wks.servicemarketplace.serviceproviderservice.core.Email
import com.wks.servicemarketplace.serviceproviderservice.core.PhoneNumber
import java.util.*
import kotlin.random.Random

fun PhoneNumber.Companion.random(): PhoneNumber {
    return of("+971" + ((1..7)
            .map { Random.nextInt(0, 9) }
            .joinToString("", transform = Int::toString)
            )
    )
}

fun Email.Companion.random(): Email {
    return of("${UUID.randomUUID()}@example.com")
}