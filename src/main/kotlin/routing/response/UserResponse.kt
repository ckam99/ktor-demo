package com.example.routing.response

import com.example.models.User
import com.example.utils.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class UserResponse(
    @Serializable(with = UUIDSerializer::class)
    var id: UUID,
    val name: String,
    val email: String,
)

fun User.toResponse(): UserResponse {
    return UserResponse(
        id = this.id,
        email = this.email,
        name = this.name,
    )
}