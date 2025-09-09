package com.example.plugins

import io.ktor.server.application.Application
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection


fun Application.configureDatabases() {


    Database.connect(
        driver = environment.config.property("database.driver").getString(),
        url = environment.config.property("database.url").getString(),
        user = environment.config.property("database.user").getString(),
        password = environment.config.property("database.password").getString(),
    )
//    transaction {
//        SchemaUtils.create(Users)
//    }

    transaction {
        // 1. Create user table
        exec("""
            CREATE TABLE IF NOT EXISTS users(
              id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
              name VARCHAR(50) NOT NULL,
              email VARCHAR(255) NOT NULL,
              password VARCHAR(255),
              role VARCHAR(25) DEFAULT 'USERS'
            );
        """)
        // 2. Add unique index to 'email':
        exec(" CREATE UNIQUE INDEX  IF NOT EXISTS users_email_key ON users USING btree (email);")
    }

//    val database = Database.connect(
//        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
//        user = "root",
//        driver = "org.h2.Driver",
//        password = "",
//    )

}