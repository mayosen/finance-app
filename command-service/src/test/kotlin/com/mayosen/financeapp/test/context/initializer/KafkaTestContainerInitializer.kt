package com.mayosen.financeapp.test.context.initializer

import org.apache.logging.log4j.kotlin.Logging
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.kafka.KafkaContainer
import org.testcontainers.utility.DockerImageName

class KafkaTestContainerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(context: ConfigurableApplicationContext) {
        if (container == null) {
            logger.info("Starting container for the first time")
            val imageName = DockerImageName.parse("apache/kafka:3.9.1")
            container = KafkaContainer(imageName)
            container!!.start()
        } else {
            logger.info("Reusing already started container")
        }

        TestPropertyValues
            .of(
                mapOf(
                    "spring.kafka.bootstrap-servers" to container!!.bootstrapServers,
                ),
            ).applyTo(context)
    }

    private companion object : Logging {
        @JvmStatic
        var container: KafkaContainer? = null
    }
}
