package com.example.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.requestvalidation.RequestValidationException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import kotlinx.serialization.Serializable

@Serializable
data class ErrorHandling(
    val error: String,
    val message: String
)

fun Application.configureErrorHandling(){
    install(StatusPages){

        exception<Throwable>{call, cause ->
            println(cause)
            return@exception  call.respond(
                status = HttpStatusCode.InternalServerError,
                message = ErrorHandling(error = "ERR_INTERNAL", message = cause.message ?: "Something wrong")
            )
        }

        exception<RequestValidationException> { call, cause ->
            println(cause)
            call.respond(HttpStatusCode.UnprocessableEntity, mapOf("errors" to cause.reasons))
        }

        status(HttpStatusCode.Unauthorized){ call, cause ->
            println(cause)
            return@status  call.respond(
                status = HttpStatusCode.Unauthorized,
                message = ErrorHandling(error = "ERR_UNAUTHORIZED", message = cause.description ?: "you are not authorized to access to resource")
            )
        }

//        statusFile(
//            HttpStatusCode.BadRequest,
//            HttpStatusCode.Unauthorized,
//            HttpStatusCode.NotFound,
//            filePattern = "template/error/#.html"
//        )
    }
}