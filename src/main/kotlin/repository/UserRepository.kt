package com.example.repository

import com.example.models.User
import java.util.UUID

class UserRepository {

    private val users = mutableListOf<User>()

    fun findAll(): List<User> = users

    fun findById(id: UUID): User? = users.firstOrNull { it.id == id }

    fun findByEmail(email: String): User? = users.firstOrNull { it.email == email }

    fun save(user: User): Boolean  = users.add(user)
}