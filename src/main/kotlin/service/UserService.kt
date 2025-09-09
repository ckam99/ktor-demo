package com.example.service

import com.example.models.User
import com.example.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class UserService(
    private val repository: UserRepository
) {
    suspend fun findAll(): List<User> =  withContext(Dispatchers.IO){ repository.findAll() }

    suspend fun findById(id: String): User? = withContext(Dispatchers.IO){ repository.findById(UUID.fromString(id))}

    suspend  fun findByEmail(email: String): User? = withContext(Dispatchers.IO){ repository.findByEmail(email)}

    suspend  fun create(user: User): User? = withContext(Dispatchers.IO) {
        val foundUser = findByEmail(user.email)
        return@withContext if (foundUser == null){
            repository.create(user)
            user
        } else null
    }

    suspend  fun save(user: User): User = withContext(Dispatchers.IO) {
        val foundUser = findByEmail(user.email)
        if (foundUser != null) {
            repository.update(foundUser.id, user)
            return@withContext user
        }
        user.id = repository.create(user)
        return@withContext user
    }

    suspend  fun authenticate(username: String, password: String): User? = withContext(Dispatchers.IO) {
       val user =  repository.findByEmail(username) ?: return@withContext null
       return@withContext if (user.password != password) null
        else user
    }
}