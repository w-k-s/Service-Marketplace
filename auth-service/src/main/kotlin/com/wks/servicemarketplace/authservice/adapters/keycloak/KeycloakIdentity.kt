package com.wks.servicemarketplace.authservice.adapters.keycloak

import com.wks.servicemarketplace.authservice.core.Identity

data class KeycloakIdentity(override val id: String) : Identity