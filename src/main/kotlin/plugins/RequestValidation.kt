package com.example.plugins

import com.example.models.SignInRequest
import com.example.models.SignUpRequest
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

        validate<SignUpRequest> { payload ->
            if (payload.name.isBlank()) ValidationResult.Invalid("Name is required")
            else if(payload.password.isBlank()) ValidationResult.Invalid("password is required")
            else if(payload.password.length < 4) ValidationResult.Invalid("password is not valid: 04 characters minimum")
            else if(payload.username.isBlank()) ValidationResult.Invalid("Email is required")
            else if(!payload.username.contains("@")) ValidationResult.Invalid("Email is not valid")
            else ValidationResult.Valid
        }

        validate<SignInRequest> { payload ->
            if (payload.username.isBlank()) ValidationResult.Invalid("Name of user is required")
            else if(payload.password.isBlank()) ValidationResult.Invalid("Email of user is required")
            else ValidationResult.Valid
        }
    }
}