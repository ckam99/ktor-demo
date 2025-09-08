package com.example.plugins

import com.example.service.JwtService
import com.example.service.UserService
import com.example.websocket.registerChatWebsocket
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import kotlin.time.Duration.Companion.seconds

fun Application.configureWebSockets(
    userService: UserService,
    jwtService: JwtService
){
    install(WebSockets){
        pingPeriod = 10.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        registerChatWebsocket(userService, jwtService)
    }
}