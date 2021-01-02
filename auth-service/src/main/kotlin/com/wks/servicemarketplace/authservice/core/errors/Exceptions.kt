package com.wks.servicemarketplace.authservice.core.errors

import com.wks.servicemarketplace.common.errors.CoreException
import com.wks.servicemarketplace.common.errors.ErrorType


data class UnauthorizedException(override val message: String) : CoreException(ErrorType.AUTHORIZATION, message)

class UserNotFoundException
    : CoreException(ErrorType.USER_NOT_FOUND, "User not found")

class LoginFailedException(message: String)
    : CoreException(ErrorType.LOGIN_FAILED, message)

class RegistrationFailedException(message: String)
    : CoreException(ErrorType.REGISTRATION_FAILED, message)

class RegistrationInProgressException(message: String)
    : CoreException(ErrorType.REGISTRATION_IN_PROGRESS, message)
