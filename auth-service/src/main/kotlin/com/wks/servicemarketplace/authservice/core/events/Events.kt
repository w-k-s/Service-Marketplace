package com.wks.servicemarketplace.authservice.core.events

import com.wks.servicemarketplace.authservice.core.User

data class AccountCreatedEvent(
        val id: String,
        val username: String,
        val firstName: String,
        val lastName: String,
        val email: String
) {
    constructor(user: User) : this(
            user.id,
            user.username,
            user.firstName,
            user.lastName,
            user.email
    )
}