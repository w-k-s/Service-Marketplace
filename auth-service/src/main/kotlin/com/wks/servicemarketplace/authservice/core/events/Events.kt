package com.wks.servicemarketplace.authservice.core.events

import com.wks.servicemarketplace.authservice.core.Identity

data class AccountCreatedEvent(
        val id: String,
        val username: String,
        val firstName: String,
        val lastName: String,
        val email: String
) {
    constructor(identity: Identity) : this(identity.id,
            identity.username,
            identity.firstName,
            identity.lastName,
            identity.email
    )
}