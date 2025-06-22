package com.mayosen.financeapp.test.context.kafka

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate

@TestConfiguration
class KafkaTestProducerConfiguration {
    @Bean
    fun eventKafkaTemplate(environment: Environment): KafkaTemplate<String, String> {
        val configProps =
            mapOf(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to environment["spring.kafka.bootstrap-servers"]!!,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            )
        val factory = DefaultKafkaProducerFactory<String, String>(configProps)
        return KafkaTemplate(factory)
    }
}
