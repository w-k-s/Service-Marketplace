package com.wks.servicemarketplace.authservice.core

interface Client {
    val clientName: String
    val permissions: List<String>
}