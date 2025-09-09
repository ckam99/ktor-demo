package com.example.repository

import com.example.generated.tables.Users
import com.example.generated.tables.records.UsersRecord
import com.example.models.User
import org.jooq.DSLContext
import java.util.UUID

class JooqUserRepository (
    private val ctx: DSLContext
) : UserRepository {

    override suspend fun create(user: User): UUID {
        val record =  ctx.insertInto(
            Users.USERS,
            Users.USERS.NAME,
            Users.USERS.EMAIL,
            Users.USERS.PASSWORD,
            Users.USERS.ROLE,
        )
            .values(
                   user.name,
                   user.email,
                   user.password,
                user.role,
            ).returning(Users.USERS.ID)
            .fetchOneInto(UsersRecord::class.java)
        user.id = record!!.id!!
        return record.id!!
    }

    override suspend fun findAll(): List<User> {
        return ctx.selectFrom(Users.USERS)
            .fetch { it?.toDomain() }
    }

    override suspend fun findById(id: UUID): User? {
                return ctx.selectFrom(Users.USERS)
            .where(Users.USERS.ID.eq(id))
            .fetchOne { it?.toDomain() }
    }

    override suspend fun findByEmail(email: String): User? {
                return ctx.selectFrom(Users.USERS)
            .where(Users.USERS.EMAIL.eq(email))
            .fetchOne { it?.toDomain() }
    }

    override suspend fun update(id: UUID, user: User) {
        ctx.update(Users.USERS)
            .set(Users.USERS.NAME, user.name)
            .set(Users.USERS.ROLE, user.role)
            .set(Users.USERS.PASSWORD, user.password)
            .where(Users.USERS.ID.eq(id))
    }

    override suspend fun delete(id: UUID) {
        ctx.delete(Users.USERS)
            .where(Users.USERS.ID.eq(id))
    }

}

fun UsersRecord.toDomain(): User {
    val user =  User(
        id = id ?: UUID.randomUUID(),
        name = name ?: "",
        email = email ?: "",
        password = password,
        role =  role ?: ""
    )
    return user
}
