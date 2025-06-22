package com.mayosen.financeapp.test.context

import org.apache.logging.log4j.kotlin.Logging
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

class PostgresqlTestContainerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(context: ConfigurableApplicationContext) {
        if (CONTAINER == null) {
            logger.info("Starting container for the first time")
            val imageName =
                DockerImageName.parse("postgres:17")
            CONTAINER =
                PostgreSQLContainer(imageName)
                    .withDatabaseName("postgres")
                    .withUsername("postgres")
                    .withPassword("postgres")
                    .withExposedPorts(5432)
            CONTAINER!!.start()
        } else {
            logger.info("Reusing already started container")
        }

        TestPropertyValues
            .of(
                mapOf(
                    "spring.datasource.url" to CONTAINER!!.jdbcUrl,
                    "spring.datasource.username" to CONTAINER!!.username,
                    "spring.datasource.password" to CONTAINER!!.password,
                    "spring.datasource.driverClassName" to CONTAINER!!.driverClassName,
                ),
            ).applyTo(context)
    }

    private companion object : Logging {
        @JvmStatic
        var CONTAINER: PostgreSQLContainer<*>? = null
    }
}
