package com.example.plugins

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.Application
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.FlywayException
import org.flywaydb.core.api.exception.FlywayValidateException
import org.jetbrains.exposed.sql.Database
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import java.sql.DriverManager


fun Application.configureDatabases(): DSLContext {

    val driver = environment.config.property("database.driver").getString()
    val url = environment.config.property("database.url").getString()
    val user = environment.config.property("database.user").getString()
    val password = environment.config.property("database.password").getString()

    val dataSource = createDataSource(url, user, password, driver)
    Database.connect(dataSource)

    println("Database successfully connected")

    val autoMigrate = environment.config.property("database.autoMigrate").getString().toBoolean()
    migrate(dataSource, driver, autoMigrate)

    return DSL.using(dataSource, SQLDialect.POSTGRES)
}

private fun createDataSource(url: String, user: String, password: String, driver: String = "org.postgresql.Driver"): HikariDataSource {
    val config = HikariConfig().apply {
        jdbcUrl = url
        username = user
        this.password = password
        driverClassName = driver
    }
    val dataSource = HikariDataSource(config)
    dataSource.connection.use { conn ->
        println(conn.metaData.databaseProductName)
        println(conn.metaData.databaseProductVersion)
    }
    return dataSource
}

private fun migrate(dataSource: HikariDataSource, driver: String, autoMigrate : Boolean = true){

    try {
        val flyway = Flyway.configure()
            //.locations("classpath:db/migration")
            .dataSource(dataSource)
            .schemas("public")
            .driver(driver)
            .baselineOnMigrate(true)
            .validateOnMigrate(false)
            .table("migrations")
            .loggers("slf4j")
            .load()


        if (autoMigrate) {
            flyway.migrate()
            println("Migration successfully executed")
        }
    } catch (e: FlywayValidateException) {
        println("⚠️ Migration validation failed: ${e.message}")
    } catch (e: FlywayException) {
        println("❌ General Flyway error: ${e.message}")
    } catch (e: Exception){
        println("Error migration: ${e.message}")
    }
}

//private fun createDslContext(url: String, user: String, password: String): DSLContext {
//  //  val connection = DriverManager.getConnection(url, user, password)
//    return DSL.using(connection, SQLDialect.POSTGRES)
//}