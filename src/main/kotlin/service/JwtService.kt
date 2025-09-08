package com.example.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.config.JwtConfig
import com.example.models.JwtToken
import io.ktor.server.auth.jwt.JWTCredential
import io.ktor.server.auth.jwt.JWTPrincipal
import java.util.Date

class JwtService(
    val config: JwtConfig,
    private val userService: UserService
) {


    val verifier = JWT.require(Algorithm.HMAC256(config.secret))
        .withAudience(config.audience)
        .withIssuer(config.issuer)
        .build()


    fun generateToken(
        username: String,
        role: String,
        expireIn: Long ? = null
    ): JwtToken {
        val expiredAt = Date(System.currentTimeMillis() + (expireIn ?: config.expiry))
        val token = JWT.create()
            .withAudience(config.audience)
            .withIssuer(config.issuer)
            .withClaim("username", username)
            .withClaim("role", role)
            .withExpiresAt(expiredAt)
            .sign(Algorithm.HMAC256(config.secret))
        return  JwtToken(token = token, expiry = expiredAt)
    }

    fun customValidator(credential: JWTCredential): JWTPrincipal ? {
        val username = extractUsername(credential)
        val user = username?.let(userService::findByEmail)
        return user?.let {
            if(audienceMatches(credential)){
                JWTPrincipal(credential.payload)
            } else null
        }
    }

    private fun audienceMatches(credential: JWTCredential): Boolean {
        return credential.payload.audience.contains(config.audience)
    }

    private fun extractUsername(credential: JWTCredential) : String? {
        return credential.payload.getClaim("username").asString()
    }


}