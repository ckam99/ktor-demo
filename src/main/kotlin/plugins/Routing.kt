package com.example.plugins

import com.example.models.SignInRequest
import com.example.models.User
import com.example.models.SignUpRequest
import com.example.models.TokenResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.random.Random

fun Application.configureRouting(config: JwtConfig) {

    val userDb = mutableMapOf<String, User>()

    routing {

        post("signup") {
            val form = call.receiveNullable<SignUpRequest>()
                ?: return@post call.respond(HttpStatusCode.BadRequest)
            if (userDb.containsKey(form.username)){
                call.respondText("Username already exists", status = HttpStatusCode.BadRequest)
            }else{
                val user = User(
                    id = Random.nextInt(),
                    name = form.name,
                    email = form.username,
                    password = form.password
                )
                userDb[form.username] =   user
                val token = generateToken(config, user.email)
                call.respond(TokenResponse(token.token, expiredAt = token.expiry.toString()))
            }
        }

        post("signin") {
            val form = call.receiveNullable<SignInRequest>()
                ?: return@post call.respond(HttpStatusCode.BadRequest)

            val user = userDb.get(form.username) ?: return@post call.respondText(
                "Username does not exists", status = HttpStatusCode.BadRequest)

            if (user.password != form.password) {
                return@post call.respondText(
                    "Invalid credentials", status = HttpStatusCode.BadRequest
                )
            }
            val token = generateToken(config, user.email)
            call.respond(TokenResponse(token.token, expiredAt = token.expiry.toString()))
        }

        authenticate("jwt-auth") {

            get("users/me") {
                val principal = call.principal<JWTPrincipal>()
                val username = principal?.payload?.getClaim("username")?.asString()
              //  val expired = principal?.expiresAt?.time?.minus(System.currentTimeMillis())

                val user = userDb.get(username) ?: return@get call.respondText(
                    "Invalid credentials: corrupted", status = HttpStatusCode.BadRequest)
                call.respond(user)
            }

            post("/users") {
                val user = call.receive<User>()
                user.id = Random.nextInt()
                call.respond(user)
            }

        }
    }
}
