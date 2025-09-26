package com.example.routing

import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.defaultRoute(){
    get{
        call.respondText("It's OK")
    }
}