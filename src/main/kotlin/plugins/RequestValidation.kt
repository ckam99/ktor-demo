package com.example.plugins

import com.example.models.User
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult

fun Application.configureRequestValidation(){
    install(RequestValidation){

        validate<User> { payload ->
            if (payload.name.isBlank()) ValidationResult.Invalid("Name of user is required")
            else if(payload.email.isBlank()) ValidationResult.Invalid("Email of user is required")
            else if(!payload.email.contains("@")) ValidationResult.Invalid("Email is not valid")
            else ValidationResult.Valid
        }
    }
}