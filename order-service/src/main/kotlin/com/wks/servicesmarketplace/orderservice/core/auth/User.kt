package com.wks.servicesmarketplace.orderservice.core.auth

import java.security.Principal

interface Authentication : Principal {
    val user: User?
    val token: String
    val roles: List<String>
}

interface User : Principal {
    val id: String?
}
