package com.example.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import org.slf4j.event.Level

fun Application.configureLogging(){
    install(CallLogging){
        level = Level.INFO
        filter { call ->
            call.request.path().startsWith("/users")
        }

        format { call ->
            val name = call.parameters["name"] ?: "Unknow"
            "User: $name, Method: ${call.request.httpMethod.value}, Path: ${call.request.path()}"
        }
    }
}