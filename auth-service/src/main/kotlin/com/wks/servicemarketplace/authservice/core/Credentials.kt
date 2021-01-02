package com.wks.servicemarketplace.authservice.core

import com.wks.servicemarketplace.common.Email
import com.wks.servicemarketplace.common.Password


interface Credentials {
    val username: Email
    val password: Password
}