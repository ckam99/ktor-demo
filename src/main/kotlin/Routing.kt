package com.example

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.request.receiveChannel
import io.ktor.server.request.receiveStream
import io.ktor.server.request.receiveText
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.copyAndClose
import io.ktor.utils.io.readRemaining
import io.ktor.utils.io.readText
import java.io.File
import java.io.FileOutputStream

fun Application.configureRouting() {


    routing {

        get("upload"){

        }

       post("/upload") {
           val file = File("uploads/sample.md")
           file.parentFile?.mkdirs()

           // method 1: from ByteArray
//           val byteArray = call.receive<ByteArray>()
//           file.writeBytes(byteArray)

           // method 2: from Stream
//           val stream = call.receiveStream()
//           FileOutputStream(file).use { fileOutputStream ->
//               stream.copyTo(fileOutputStream, bufferSize = 24*1024)
//           }

           // method 3: from Channel
           val channel = call.receiveChannel()
           channel.copyAndClose(file.writeChannel())
           call.respondText("File successfully upload, ${file.absolutePath}")
       }
    }
}
