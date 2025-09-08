package com.example.routing

import com.example.routing.request.SignInRequest
import com.example.routing.request.SignUpRequest
import com.example.routing.response.TokenResponse
import com.example.models.User
import com.example.service.JwtService
import com.example.service.UserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.authRoute(
    userService: UserService,
    jwtService: JwtService
) {

    post("/signup") {
        val form = call.receiveNullable<SignUpRequest>()
            ?: return@post call.respond(HttpStatusCode.BadRequest)

        var user = userService.findByEmail(form.username)
        if (user != null) {
            return@post call.respondText("Username already exists", status = HttpStatusCode.BadRequest)
        }
         userService.save(User(
             name = form.name,
             email = form.username,
             password = form.password
         ))

        val token = jwtService.generateToken(form.username)
            ?:  return@post call.respondText(
                "Invalid credentials", status = HttpStatusCode.Unauthorized
            )
        call.respond(TokenResponse(token.token, expiredAt = token.expiry.toString()))
    }

    post("/signin") {
        val form = call.receiveNullable<SignInRequest>()
            ?: return@post call.respond(HttpStatusCode.BadRequest)
        val user = userService.authenticate(form.username, form.password)
            ?:  return@post call.respondText(
                "Invalid credentials", status = HttpStatusCode.Unauthorized
            )
       val token = jwtService.generateToken(user.email)
           ?:  return@post call.respondText(
               "Invalid credentials", status = HttpStatusCode.Unauthorized
           )
        call.respond(TokenResponse(token.token, expiredAt = token.expiry.toString()))
    }
}