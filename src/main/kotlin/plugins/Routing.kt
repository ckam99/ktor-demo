package com.example.plugins

import com.example.User
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.application.*
import io.ktor.server.application.install
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.request.receiveNullable
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.copyAndClose
import java.io.File
import kotlin.random.Random

fun Application.configureRouting() {
    routing {

        route("message") {

            // not exported
            install(RequestValidation) {
                validate<String> { payload ->
                    if (payload.isBlank()) ValidationResult.Invalid("Data should not be empty")
                    else ValidationResult.Valid
                }
            }

            get("/") {
                val msg = call.receive<String>()
                call.respondText(msg)
            }
        }


        post ("/users") {
            val user = call.receive<User>()
            user.id = Random.nextInt()
            call.respond(user)
        }
    }
}
