package com.example.plugins

import com.example.models.Message
import com.example.models.User
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
import io.ktor.server.sse.sse
import io.ktor.server.websocket.webSocket
import io.ktor.sse.ServerSentEvent
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.copyAndClose
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

fun Application.configureRouting() {

    val onlineUsers = ConcurrentHashMap<String, WebSocketSession>()
    routing {


        webSocket("/chat"){
            val username = call.request.queryParameters["username"] ?: run {
                this.close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "username is required to establish connexion"))
                return@webSocket
            }

            onlineUsers[username] = this

            send("You are connected")

            try {
                incoming.consumeEach { frame ->
                    if (frame is Frame.Text){
                        val message = Json.decodeFromString<Message>(frame.readText())
                        if (message.to.isNullOrBlank()){
                            onlineUsers.values.forEach {
                                it.send("$username: ${message.text}")
                            }
                        }else{
                            val session = onlineUsers[message.to]
                            session?.send("$username: ${message.text}")
                        }
                    }
                }
            }finally {
                onlineUsers.remove(username)
                this.close()
            }



        }

        sse("events"){
            repeat(6){
                send(ServerSentEvent("Event: ${it + 1}"))
                delay(1000L)
            }
        }

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
