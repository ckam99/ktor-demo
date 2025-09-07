package com.example

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receiveText
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {


    routing {

        get("/") {
            call.respondText("Hello World!")
        }

        get("/greet/{name}"){
            val name = call.pathParameters["name"]
            if (name == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            call.respondText("Hello, $name")
        }

        post("/greet") {
            val name = call.receiveText()
            var message = "Hello, $name"
            val age = call.queryParameters["age"]
            age?.let { message += ", you are $age old" }
            call.respondText(message)
        }
    }
}
