package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respondText
import java.util.Date

fun Application.configureJWTAuthentication(config: JwtConfig) {
    install(Authentication){
        jwt("jwt-auth") {
            realm = config.realm

            val jwtVerifier = JWT.require(Algorithm.HMAC256(config.secret))
                .withAudience(config.audience)
                .withIssuer(config.issuer)
                .build()

            verifier(jwtVerifier)

            validate { credentials ->
                val username = credentials.payload.getClaim("username").asString()
                if (username.isNotBlank()){
                    JWTPrincipal(credentials.payload)
                }else null
            }

            challenge { _, _ ->
                call.respondText("Token is null or expired",
                    status = HttpStatusCode.Unauthorized)
            }

        }
    }
}

data class JwtConfig(
    val secret: String,
    val issuer: String,
    val audience: String,
    val realm: String,
    val expiry: Long
)

data class JwtToken(
    val token: String,
    val expiry: Date
)

fun generateToken(config: JwtConfig, username: String): JwtToken {
    val expiredAt = Date(System.currentTimeMillis() + config.expiry)
    val token =  JWT.create()
        .withAudience(config.audience)
        .withIssuer(config.issuer)
        .withClaim("username", username)
        .withExpiresAt(expiredAt)
        .sign(Algorithm.HMAC256(config.secret))
    return JwtToken(token = token, expiry = expiredAt)
}