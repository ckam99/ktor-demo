package com.example.routing.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    @SerialName("token")
    val token: String,
    @SerialName("expired_at")
    val expiredAt: String,
)