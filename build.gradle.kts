plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.serialization)

    alias(libs.plugins.jooq.codegen.gradle)
    alias(libs.plugins.flyway.plugin)
}

group = "com.example"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(libs.flyway.database.postgresql)
    }
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)

    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.request.validation)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.websockets)

    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)

    implementation(libs.postgresql)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.h2)
    implementation(libs.flyway.core)

    implementation(libs.jooq.core)
    jooqCodegen(libs.postgresql)

    implementation(libs.hikari)

}


val dotenvFile = file(".env")
val envMap = dotenvFile.takeIf { it.exists() }?.readLines()
    ?.filter { it.isNotBlank() && !it.startsWith("#") }
    ?.associate {
        val (k, v) = it.split("=", limit = 2)
        k.trim() to v.trim()
    } ?: emptyMap()

fun env(key: String, default: String = ""): String =
    System.getenv(key) ?: envMap[key] ?: default

val dbHost = env("DB_HOST", "localhost")
val dbPort = env("DB_PORT", "5432")
val dbName = env("DB_NAME", "test")
val dbUser = env("DB_USER", "postgres")
val dbPassword = env("DB_PASSWORD", "")
val dbUrl = "jdbc:postgresql://$dbHost:$dbPort/$dbName"
val migrationTable = "migrations"
flyway {
    driver = "org.postgresql.Driver"
    url = dbUrl
    user = dbUser
    password = dbPassword
    schemas = arrayOf("public")
    locations = arrayOf("filesystem:src/main/resources/db/migration")
    baselineOnMigrate = true
    table = migrationTable
}

jooq {
    configuration {
        jdbc {
            driver = "org.postgresql.Driver"
            url = dbUrl
            user = dbUser
            password = dbPassword
        }
        generator {
            name = "org.jooq.codegen.KotlinGenerator"
            database {
                name = "org.jooq.meta.postgres.PostgresDatabase"
                inputSchema = "public"
                includes = ".*"
                excludes = migrationTable
            }
            target {
                packageName = "com.example.generated"
                directory = "$buildDir/generated/jooq"
            }
            //generate {
//                pojos = false
//                pojosToString = false
//                pojosEqualsAndHashCode = false
//                daos = false
//                fluentSetters = false
//                immutablePojos = false
//            }
        }
    }
}

tasks.named("jooqCodegen") {
    dependsOn(tasks.named("flywayMigrate"))
}

sourceSets {
    main {
        kotlin {
            srcDir("$buildDir/generated/jooq")
        }
    }
}