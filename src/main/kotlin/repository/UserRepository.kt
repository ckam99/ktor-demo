package com.example.repository

import com.example.models.User
import java.util.UUID

interface UserRepository {


    suspend fun create(user: User): UUID

    suspend fun findAll(): List<User>

    suspend fun findById(id: UUID): User?

    suspend fun findByEmail(email: String): User?

    suspend fun update(id: UUID, user: User)

    suspend fun delete(id: UUID)

}
