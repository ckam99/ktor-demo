package com.example.models

import com.example.utils.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class User(
    @Serializable(with = UUIDSerializer::class)
    var id: UUID = UUID.randomUUID(),
    val name: String,
    val email: String,
    val password: String ? = null
)