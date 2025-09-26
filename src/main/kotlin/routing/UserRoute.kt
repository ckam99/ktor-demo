package com.example.routing

import com.example.models.User
import com.example.plugins.authorized
import com.example.routing.request.UserRequest
import com.example.routing.request.toModel
import com.example.routing.response.UserResponse
import com.example.routing.response.toResponse
import com.example.service.UserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.get
import io.ktor.server.routing.post



fun Route.userRoute(
    userService: UserService
){

    suspend fun extractPrincipalUsername(call: RoutingCall): User? {
        val username = call.principal<JWTPrincipal>()
            ?.payload
            ?.getClaim("username")?.asString()
            ?: return null
        return userService.findByEmail(username)
    }

    post {
        val body = call.receive<UserRequest>()
        val createdUser = userService.save(
            user = body.toModel()
        )
        call.response.header(name = "id", value = createdUser.id.toString())
        call.respond(HttpStatusCode.Created)
    }

    /**
     * Get all users.
     *
     * @tags *Users
     * @description  API allows to fetch all users
     * @query email The Email of the user
     * @response 200 array [UserResponse] The user.
     */
    get {
        val users = userService.findAll().map { it.toResponse() }
        call.respond(users)
    }

   authenticate {
// // * @security BearerAuth, ApiKeyAuth
       /**
        * Get current user.
        *
        * @description  get connected user
        * @tags users, catalog 🏷️
        * @security BearerAuth
        * @response 200 [UserResponse] The user.
        */
       get("/me"){
           val user = extractPrincipalUsername(call)
               ?: return@get call.respond(HttpStatusCode.Unauthorized)
           call.respond( user.toResponse())
       }

       authorized("ADMIN"){
           get("/admin"){
               call.respondText("hello admin")
           }
       }

       /**
        * Get a single user.
        *
        * @tags *Users
        * @description  API allows to find user by id.
        * @path id The ID of the user
        * @response 404 The user was not found
        * @response 200 [UserResponse] The user.
        */
       get("/{id}") {
           val id = call.parameters["id"]
               ?: return@get call.respond(HttpStatusCode.BadRequest)

           val user = userService.findById(id)
               ?: return@get call.respond(HttpStatusCode.NotFound)

           call.respond(user.toResponse())
       }
   }


}


