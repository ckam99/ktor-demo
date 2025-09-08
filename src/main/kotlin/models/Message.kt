package com.example.models

import kotlinx.serialization.Serializable


@Serializable
data class Message(
    val text: String,
    val to: String ? = null,
)