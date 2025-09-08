package com.example.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    val accessToken: String,
    @SerialName("expired_at")
    val expiredAt: String,
)
