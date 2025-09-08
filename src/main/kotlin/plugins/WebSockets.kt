package com.example.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import kotlin.time.Duration.Companion.seconds

fun Application.configureWebSockets(){
    install(WebSockets){
        pingPeriod = 10.seconds // Garder la connexion active
        timeout = 15.seconds // Déconnexion si inactivité trop longue

        maxFrameSize = Long.MAX_VALUE // Accepter de très grandes trames

        masking = false // Ne pas masquer les messages serveur
    }
}