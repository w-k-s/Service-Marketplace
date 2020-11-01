package com.wks.servicemarketplace.authservice.core.iam

import com.wks.servicemarketplace.authservice.core.Token

data class StandardToken(override val accessToken: String, override val refreshToken: String? = null) : Token