package com.example.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.auth.AuthenticationChecked
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route

class PluginConfiguration {
    var roles : Set<String> = emptySet()
}

val RoleBasedAuthentication = createRouteScopedPlugin(
    name = "RbacPlugin",
    createConfiguration = ::PluginConfiguration
){
    val roles = pluginConfig.roles
    pluginConfig.apply {
        on(AuthenticationChecked){call ->
            val role = getRoleFromToken(call)
            val autorized = roles.contains(role)
            if (!autorized){
                println("The user does not haveany the following roles: $roles")
                call.respond(HttpStatusCode.Forbidden)
            }
        }
    }
}

private fun getRoleFromToken(call: ApplicationCall): String? {
  return  call.principal<JWTPrincipal>()
        ?.payload?.getClaim("role")
        ?.asString()
}

fun Route.authorized(
    vararg roles: String,
    build: Route.() -> Unit
){
    install(RoleBasedAuthentication){
        this.roles = roles.toSet()
    }
    build()
}