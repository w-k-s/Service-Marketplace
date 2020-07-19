package com.wks.servicemarketplace.authservice.core

interface Token {
    val accessToken: String
    val refreshToken: String?
}