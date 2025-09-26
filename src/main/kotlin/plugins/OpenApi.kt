package com.example.plugins

import io.ktor.server.application.Application
import io.ktor.server.plugins.openapi.openAPI
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.routing.routing


fun Application.configureOpenApi(){
    routing {
        openAPI(
            path = "/docs",
            swaggerFile = "openapi/generated.json"
        )

        swaggerUI(
            path = "/docs/swagger",
            swaggerFile = "openapi/generated.json"
        ){
           // version = "4.15.5"
        }
    }
}