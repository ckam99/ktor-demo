package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    var id: Int ? = null,
    val name: String,
    val email: String,
)