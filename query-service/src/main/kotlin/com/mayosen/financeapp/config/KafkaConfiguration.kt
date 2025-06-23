package com.mayosen.financeapp.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.mayosen.financeapp.event.Event
import com.mayosen.financeapp.event.kafka.HeaderAwareJsonTypeResolver
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.CommonErrorHandler
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.listener.MessageListenerContainer
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
import org.springframework.kafka.support.serializer.JsonDeserializer
import java.lang.Exception

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
        val factory = ConcurrentKafkaListenerContainerFactory<String, Event>()
        factory.consumerFactory = consumerFactory
        factory.setConcurrency(1)
        factory.setAutoStartup(true)
        factory.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL_IMMEDIATE

        factory.setCommonErrorHandler(
            // TODO: Use default one
            object : CommonErrorHandler {
                override fun handleRemaining(
                    thrownException: Exception,
                    records: MutableList<ConsumerRecord<*, *>>,
                    consumer: Consumer<*, *>,
                    container: MessageListenerContainer,
                ) {
                    super.handleRemaining(thrownException, records, consumer, container)
                }

                override fun handleOne(
                    thrownException: Exception,
                    record: ConsumerRecord<*, *>,
                    consumer: Consumer<*, *>,
                    container: MessageListenerContainer,
                ): Boolean = super.handleOne(thrownException, record, consumer, container)

                override fun handleOtherException(
                    thrownException: Exception,
                    consumer: Consumer<*, *>,
                    container: MessageListenerContainer,
                    batchListener: Boolean,
                ) {
                    super.handleOtherException(thrownException, consumer, container, batchListener)
                }
            },
        )
        return factory
    }

    companion object {
        const val LISTENER_CONTAINER_FACTORY = "eventListenerContainerFactory"
    }
}
