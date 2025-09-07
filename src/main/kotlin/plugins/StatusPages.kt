package com.example.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.statuspages.exception
import io.ktor.server.plugins.statuspages.statusFile
import io.ktor.server.response.respondText

fun Application.configureStatusPage(){
    install(StatusPages){

        exception<Throwable>{call, cause ->
            call.respondText("500: ${cause.message}", status = HttpStatusCode.InternalServerError)
        }

        status(HttpStatusCode.Unauthorized){ call, _ ->
            call.respondText("401: you are not authorized to access to resource", status = HttpStatusCode.Unauthorized)
        }

        statusFile(
            HttpStatusCode.BadRequest,
            HttpStatusCode.Unauthorized,
            HttpStatusCode.NotFound,
            filePattern = "template/error/#.html"
        )
    }
}