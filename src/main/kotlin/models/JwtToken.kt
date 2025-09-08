package com.example.models

import java.util.Date

data class JwtToken(
    val token: String,
    val expiry: Date
)