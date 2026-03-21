package io.plugwerk.server

import org.testcontainers.containers.PostgreSQLContainer

object SharedPostgresContainer {
    val instance: PostgreSQLContainer<*> =
        PostgreSQLContainer("postgres:18-alpine").apply {
            start()
        }
}
