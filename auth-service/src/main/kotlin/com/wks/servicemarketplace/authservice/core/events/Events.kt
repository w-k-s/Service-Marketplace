package com.wks.servicemarketplace.authservice.core.events

import com.wks.servicemarketplace.authservice.core.User

data class AccountCreatedEvent(
        val uuid: String,
        val username: String,
        val firstName: String,
        val lastName: String,
        val email: String,
        val mobileNumber: String
) {
    constructor(user: User) : this(
            user.id,
            user.username,
            user.firstName,
            user.lastName,
            user.email,
            user.mobileNumber
    )
}