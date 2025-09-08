package com.example.models

import kotlinx.serialization.Serializable


@Serializable
data class SignUpRequest(
    val name: String,
    val username: String,
    val password: String,
)

@Serializable
data class SignInRequest(
    val username: String,
    val password: String,
)