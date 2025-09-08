package com.example.service

import com.example.models.User
import com.example.repository.UserRepository
import java.util.UUID

class UserService(
    private val repository: UserRepository
) {
    fun findAll(): List<User> =  repository.findAll()

    fun findById(id: String): User? = repository.findById(UUID.fromString(id))

    fun findByEmail(email: String): User? = repository.findByEmail(email)

    fun save(user: User): User? {
        val foundUser = findByEmail(user.email)
        return if (foundUser == null){
            repository.save(user)
            user
        } else null
    }

    fun authenticate(username: String, password: String): User? {
       val user =  repository.findByEmail(username) ?: return null
       return if (user.password != password) return null
        else user
    }
}