package com.mayosen.financeapp.test.context.initializer

import org.apache.logging.log4j.kotlin.Logging
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

class PostgresqlTestContainerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(context: ConfigurableApplicationContext) {
        if (container == null) {
            logger.info("Starting container for the first time")
            val imageName =
                DockerImageName.parse("postgres:17")
            container =
                PostgreSQLContainer(imageName)
                    .withDatabaseName("postgres")
                    .withUsername("postgres")
                    .withPassword("postgres")
                    .withExposedPorts(5432)
            container!!.start()
        } else {
            logger.info("Reusing already started container")
        }

        TestPropertyValues
            .of(
                mapOf(
                    "spring.datasource.url" to container!!.jdbcUrl,
                    "spring.datasource.username" to container!!.username,
                    "spring.datasource.password" to container!!.password,
                ),
            ).applyTo(context)
    }

    private companion object : Logging {
        @JvmStatic
        var container: PostgreSQLContainer<*>? = null
    }
}
