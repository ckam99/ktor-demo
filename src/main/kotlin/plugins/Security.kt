package com.example.plugins

import com.example.service.JwtService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respondText

fun Application.configureSecurity(
    jwtService: JwtService
) {
    authentication {
        jwt{
            realm = jwtService.config.realm
            verifier(jwtService.verifier)
            validate { credential ->
                jwtService.customValidator(credential)
            }

            challenge { _, _ ->
                call.respondText("Token is null or expired",
                    status = HttpStatusCode.Unauthorized)
            }

        }
    }
}




