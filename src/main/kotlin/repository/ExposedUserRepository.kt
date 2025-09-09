package com.example.repository

import com.example.models.User
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update
import java.util.UUID

class ExposedUserRepository() : UserRepository {

   override  suspend fun create(user: User): UUID = dbQuery {
        Users.insert {
            it[name] = user.name
            it[email] = user.email
            it[password] = user.password
            it[role] = user.role
        }[Users.id]
    }

    override suspend fun findAll(): List<User> {
        return dbQuery {
            Users.selectAll()
                .map{it.toUser()}
        }
    }

    override suspend fun findById(id: UUID): User? {
        return dbQuery {
            Users.selectAll()
                .where { Users.id eq id }
                .limit(1)
                .map{it.toUser()}
                .singleOrNull()
        }
    }

    override suspend fun findByEmail(email: String): User? {
        return dbQuery {
            Users.selectAll()
                .where { Users.email eq email }
                .limit(1)
                .map{it.toUser()}
                .singleOrNull()
        }
    }

    override suspend fun update(id: UUID, user: User) {
        dbQuery {
            Users.update({ Users.id eq id }) {
                it[name] = user.name
                it[password] = user.password
                it[role] = user.role
            }
        }
    }

    override suspend fun delete(id: UUID) {
        dbQuery {
            Users.deleteWhere { Users.id.eq(id) }
        }
    }

    private  suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

}

private object Users : Table() {
    val id = uuid("id").clientDefault { UUID.randomUUID() }
    val name = varchar("name", length = 50)
    val email = varchar("email", length = 255,).uniqueIndex()
    val password  = varchar("password", length = 255).nullable()
    val role = varchar("role", length = 25).default("USER")
    override val primaryKey = PrimaryKey(id)
}

private fun ResultRow.toUser(): User {
    return User(
        id = this.get(Users.id),
        name = this.get(Users.name),
        email = this.get(Users.email),
        password = this.get(Users.password),
        role = this.get(Users.role),
    )
}