package com.example.websocket

import com.auth0.jwt.exceptions.JWTVerificationException
import com.example.models.Message
import com.example.service.JwtService
import com.example.service.UserService
import io.ktor.server.routing.Routing
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

val onlineUsers = ConcurrentHashMap<String, WebSocketSession>()

fun Routing.registerChatWebsocket(
    userService: UserService,
    jwtService: JwtService
) {

    webSocket("/chat"){

        val token = call.request.queryParameters["token"] ?: run {
            println("token is required to establish connexion")
            this.close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT,
                "token is required to establish connexion"))
            return@webSocket
        }

        val decodedJWT = try {
            jwtService.verifier.verify(token)
        } catch (e: JWTVerificationException) {
            println("Invalid token")
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Invalid token"))
            return@webSocket
        }

        val username = decodedJWT.getClaim("id")?.asString() ?: run {
            println("Invalid user info")
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Invalid user info"))
            return@webSocket
        }

        userService.findByEmail(username) ?: run {
            println("username is required to establish connexion: user does not exists")
            this.close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT,
                "username is required to establish connexion: user does not exists"))
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
}