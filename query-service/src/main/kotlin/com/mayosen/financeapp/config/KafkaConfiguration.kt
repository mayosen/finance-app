package com.mayosen.financeapp.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.mayosen.financeapp.event.Event
import com.mayosen.financeapp.event.kafka.HeaderAwareJsonTypeResolver
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.CommonLoggingErrorHandler
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
import org.springframework.kafka.support.serializer.JsonDeserializer

@Configuration
class KafkaConfiguration {
    @Bean
    fun eventDeserializer(
        objectMapper: ObjectMapper,
        headerAwareJsonTypeResolver: HeaderAwareJsonTypeResolver,
    ): Deserializer<Event> {
        val jsonDeserializer =
            JsonDeserializer(Event::class.java, objectMapper).apply {
                setTypeResolver(headerAwareJsonTypeResolver)
                addTrustedPackages(Event::class.java.packageName)
            }
        return ErrorHandlingDeserializer(jsonDeserializer)
    }

    @Bean
    fun headerAwareJsonTypeResolver(objectMapper: ObjectMapper): HeaderAwareJsonTypeResolver = HeaderAwareJsonTypeResolver(objectMapper)

    @Bean
    fun consumerFactory(
        kafkaProperties: KafkaProperties,
        eventDeserializer: Deserializer<Event>,
    ): ConsumerFactory<String, Event> {
        val props = kafkaProperties.buildConsumerProperties()
        return DefaultKafkaConsumerFactory(
            props,
            StringDeserializer(),
            eventDeserializer,
        )
    }

    @Bean(LISTENER_CONTAINER_FACTORY)
    fun kafkaListenerContainerFactory(
        consumerFactory: ConsumerFactory<String, Event>,
    ): ConcurrentKafkaListenerContainerFactory<String, Event> {
        val commonErrorHandler =
            CommonLoggingErrorHandler()
                .apply {
                    isAckAfterHandle = false
                }

        return ConcurrentKafkaListenerContainerFactory<String, Event>()
            .apply {
                this.consumerFactory = consumerFactory
                setConcurrency(1)
                setAutoStartup(true)
                containerProperties.ackMode = ContainerProperties.AckMode.MANUAL_IMMEDIATE
                setCommonErrorHandler(commonErrorHandler)
            }
    }

    companion object {
        const val LISTENER_CONTAINER_FACTORY = "eventListenerContainerFactory"
    }
}
