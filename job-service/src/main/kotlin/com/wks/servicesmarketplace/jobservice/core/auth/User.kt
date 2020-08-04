package com.wks.servicesmarketplace.jobservice.core.auth

import org.springframework.security.core.GrantedAuthority
import java.security.Principal

interface Role : GrantedAuthority {
    val name: String
}

interface User : Principal {
    val uuid: String
    val username: String
    val roles: List<Role>
}
