package com.example

import com.example.plugins.configureLogging
import com.example.plugins.configureRequestValidation
import com.example.plugins.configureRouting
import com.example.plugins.configureSerialization
import com.example.plugins.configureStatusPage
import com.example.plugins.configureWebSockets
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureLogging()
    configureWebSockets()
    configureRouting()
    configureSerialization()
    configureStatusPage()
    configureRequestValidation()
}
