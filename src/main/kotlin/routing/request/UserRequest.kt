package com.example.routing.request

import com.example.models.User
import com.example.routing.response.UserResponse
import kotlinx.serialization.Serializable

@Serializable
data class UserRequest(
    val name: String,
    val email: String,
    val role: String,
    val password: String ? = null
)

fun UserRequest.toModel(): User {
    return User(
        email = this.email,
        name = this.name,
        role = this.role,
        password = this.password
    )
}

