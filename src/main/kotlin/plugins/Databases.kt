package com.example.plugins

import io.ktor.server.application.Application
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database



fun Application.configureDatabases() {

    val driver = environment.config.property("database.driver").getString()
    val url = environment.config.property("database.url").getString()
    val user = environment.config.property("database.user").getString()
    val password = environment.config.property("database.password").getString()


    Database.connect(
        driver = driver,
        url = url,
        user = user,
        password = password,
    )

    println("Database successfully connected")

    val autoMigrate = environment.config.property("database.autoMigrate").getString().toBoolean()

    val flyway = Flyway.configure()
        //.locations("classpath:db/migration")
        .schemas("public")
        .driver(driver)
        .baselineOnMigrate(true)
        .table("migrations")
        .dataSource(url, user, password)
        .load()

    if (autoMigrate) {
        flyway.migrate()
        println("Migration successfully executed")
    }

}