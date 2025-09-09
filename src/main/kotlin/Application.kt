package com.example

import com.example.config.Config
import com.example.config.JwtConfig
import com.example.plugins.configureDatabases
import com.example.plugins.configureSecurity
import com.example.plugins.configureLogging
import com.example.plugins.configureRequestValidation
import com.example.plugins.configureRouting
import com.example.plugins.configureSerialization
import com.example.plugins.configureErrorHandling
import com.example.plugins.configureWebSockets
import com.example.repository.ExposedUserRepository
import com.example.repository.JooqUserRepository
import com.example.repository.UserRepository
import com.example.service.JwtService
import com.example.service.UserService
import io.ktor.server.application.*
import io.ktor.server.config.getAs
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import java.sql.DriverManager

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    val config = Config(
        jwt = JwtConfig(
            secret = environment.config.property("jwt.secret").getString(),
            audience = environment.config.property("jwt.audience").getString(),
            issuer = environment.config.property("jwt.issuer").getString(),
            realm = environment.config.property("jwt.realm").getString(),
            expiry = environment.config.property("jwt.expiry").getAs() as Long,
        )
    )

    val dsl: DSLContext = configureDatabases()

     // val userRepository = ExposedUserRepository()
     val userRepository = JooqUserRepository(dsl)
    val userService = UserService(userRepository)
    val jwtService = JwtService(config = config.jwt, userService = userService)

    configureSecurity(jwtService)
    configureLogging()
    configureWebSockets(userService, jwtService)
    configureSerialization()
    configureRouting(userService, jwtService)
    configureErrorHandling()
    configureRequestValidation()
}


