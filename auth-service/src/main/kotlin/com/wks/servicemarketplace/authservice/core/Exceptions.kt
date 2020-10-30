package com.wks.servicemarketplace.authservice.core

data class UserNotFoundException(val username: String) : RuntimeException("'$username' is not a registered user")
data class DuplicateUsernameException(val username: String) : RuntimeException("'$username' is already registered")
class UnauthorizedException : RuntimeException("Unauthorized")