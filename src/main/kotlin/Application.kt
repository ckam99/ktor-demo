package com.example

import com.example.plugins.JwtConfig
import com.example.plugins.configureJWTAuthentication
import com.example.plugins.configureLogging
import com.example.plugins.configureRequestValidation
import com.example.plugins.configureRouting
import com.example.plugins.configureSSE
import com.example.plugins.configureSerialization
import com.example.plugins.configureStatusPage
import com.example.plugins.configureWebSockets
import io.ktor.server.application.*
import io.ktor.server.config.getAs

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    val jwtVar = environment.config.config("ktor.jwt")

    val jwtConfig = JwtConfig(
        secret = jwtVar.property("secret").getString(),
        audience = jwtVar.property("audience").getString(),
        issuer = jwtVar.property("issuer").getString(),
        realm = jwtVar.property("realm").getString(),
        expiry = jwtVar.property("expiry").getAs() as Long,
    )

    println(jwtConfig)

    configureJWTAuthentication(jwtConfig)
    configureLogging()
    configureWebSockets()
    configureSSE()
    configureRouting(jwtConfig)
    configureSerialization()
    configureStatusPage()
    configureRequestValidation()
}
