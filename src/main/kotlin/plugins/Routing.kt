package com.example.plugins

import com.example.User
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.application.*
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

        get("/error"){
            throw Exception("Some error occured")
        }
        get("/error/bad"){
            call.respond(HttpStatusCode.BadRequest)
        }
        get("/error/unauthorized"){
            call.respond(HttpStatusCode.Unauthorized)
        }
        get("/error/notfound"){
            call.respond(HttpStatusCode.NotFound)
        }

        post("/users"){
            val user = call.receiveNullable<User>() ?: return@post call.respond(HttpStatusCode.BadRequest)
            user.id = Random.nextInt(1, Int.MAX_VALUE)
            call.respond(user)
        }

        post("/users/form-url"){
            val form = call.receiveParameters()
            val name = form["name"] ?: ""
            val email = form["email"] ?: ""
            val user = User(name = name, email = email)
            user.id = Random.nextInt(1, Int.MAX_VALUE)
            call.respond(user)
        }

        post("/users/multipart"){
            val parts = call.receiveMultipart(formFieldLimit = 1024*1024*20) // limit to 20Mb

            val fields = mutableMapOf<String, MutableList<String>>()

            parts.forEachPart { part ->
                when(part){
                    is PartData.FormItem -> {
                        val key = part.name ?: return@forEachPart
                        fields.getOrPut(key){mutableListOf() }.add(part.value)
                        part.dispose()
                    }
                    is PartData.FileItem -> {
                        val key = part.name ?: return@forEachPart
                        val filename = part.originalFileName ?: return@forEachPart
                        fields.getOrPut(key){mutableListOf() }.add(filename)
                        val file = File("uploads/$filename").apply {
                            parentFile?.mkdirs()
                        }
                        part.provider().copyAndClose(file.writeChannel())
                        part.dispose()

                    }
                    else -> {

                    }
                }
            }

            call.respond("Form data: $fields")
        }

    }
}
