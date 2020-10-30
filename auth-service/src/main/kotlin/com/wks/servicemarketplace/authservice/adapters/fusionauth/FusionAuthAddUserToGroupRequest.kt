package com.wks.servicemarketplace.authservice.adapters.fusionauth

import com.fasterxml.jackson.annotation.JsonProperty

class FusionAuthAddUserToGroupRequest(groupId: String, userId: String) {

    data class UserId(@JsonProperty("userId") val userId: String)

    @JsonProperty("members")
    val members = mapOf(groupId to listOf(UserId(userId)))
}