package com.example.plugins

import com.example.User
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    routing {

        post("/users"){
           val user = call.receiveNullable<User>() ?: return@post call.respond(HttpStatusCode.BadRequest)
            user.id = 211
            call.respond(user)
        }

    }
}
